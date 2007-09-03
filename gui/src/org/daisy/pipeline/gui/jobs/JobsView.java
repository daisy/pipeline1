/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.gui.jobs;

import java.util.HashMap;
import java.util.Map;

import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.JobParameter;
import org.daisy.pipeline.gui.IActionConstants;
import org.daisy.pipeline.gui.model.JobManager;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.ViewPart;

/**
 * The implementation of the Jobs view used to display the queue of Pipeline
 * Jobs.
 * <p>
 * This view consists in a list of jobs displayed in a tree widget. The
 * parameters of each job can be displayed by expanding the job tree node. The
 * order of the list can be edited with move up/down actions.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public class JobsView extends ViewPart {
	/** The ID of this view (as used in plugin.xml) */
	public static final String ID = "org.daisy.pipeline.gui.views.jobs"; //$NON-NLS-1$
	/** The name of the property used to notify move actions */
	public static final int PROP_SEL_JOB_INDEX = 1;
	/** The names of the table columns */
	private static final String[] columnNames = { Messages.heading_jobs,
			Messages.heading_status };
	/** The weight of the table columns */
	private static final int[] columnWeight = { 4, 1 };
	/** The main JFace viewer used for the job list */
	private TreeViewer jobsViewer;
	/**
	 * The map holding the max name length of a job's parameters, for pretty
	 * printing
	 */
	private Map<Job, Integer> paramNameLength = new HashMap<Job, Integer>();

	/**
	 * Creates the {@link IAction}s used in this view.
	 */
	private void createActions() {
		// Create actions
		IAction clearFinishedAction = new ClearFinishedAction(this);
		IAction deleteAction = new DeleteAction(this);
		IAction moveToTopAction = new MoveToTopAction(this);
		IAction moveUpAction = new MoveUpAction(this);
		IAction moveDownAction = new MoveDownAction(this);
		IAction moveToBottomAction = new MoveToBottomAction(this);

		// Configure the tool bar
		IToolBarManager toolBar = getViewSite().getActionBars()
				.getToolBarManager();
		toolBar.add(moveToTopAction);
		toolBar.add(moveUpAction);
		toolBar.add(moveDownAction);
		toolBar.add(moveToBottomAction);
		toolBar.add(deleteAction);
		toolBar.add(clearFinishedAction);

		// Configure the retargetable actions
		getViewSite().getActionBars().setGlobalActionHandler(
				IActionConstants.MOVE_TO_TOP, moveToTopAction);
		getViewSite().getActionBars().setGlobalActionHandler(
				IActionConstants.MOVE_UP, moveUpAction);
		getViewSite().getActionBars().setGlobalActionHandler(
				IActionConstants.MOVE_DOWN, moveDownAction);
		getViewSite().getActionBars().setGlobalActionHandler(
				IActionConstants.MOVE_TO_BOTTOM, moveToBottomAction);
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.DELETE.getId(), deleteAction);

		// Hook into Undo/Redo
		IUndoContext undoContext = PlatformUI.getWorkbench()
				.getOperationSupport().getUndoContext();
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.UNDO.getId(),
				new UndoActionHandler(getSite(), undoContext));
		getViewSite().getActionBars().setGlobalActionHandler(
				ActionFactory.REDO.getId(),
				new RedoActionHandler(getSite(), undoContext));
	}

	@Override
	public void createPartControl(Composite parent) {
		// Create the tree
		Tree jobsTree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI | SWT.FULL_SELECTION);
		jobsTree.setHeaderVisible(true);
		// jobsTree.setLinesVisible(true);

		// Configure the columns
		TableLayout layout = new TableLayout();
		jobsTree.setLayout(layout);
		for (int i = 0; i < columnNames.length; i++) {
			layout.addColumnData(new ColumnWeightData(columnWeight[i], true));
			TreeColumn tc = new TreeColumn(jobsTree, SWT.NONE, i);
			tc.setText(columnNames[i]);
			tc.setMoveable(true);
		}

		// Hook painting of parameters to vertically align values
		jobsTree.addListener(SWT.PaintItem, new Listener() {
			public void handleEvent(Event e) {
				if ((e.index == 0)
						&& (e.item.getData() instanceof JobParameter)) {
					paintParam((JobParameter) e.item.getData(), e);
				}
			}
		});

		// TODO add popup menu to jobInfos table
		// jobsTable.setMenu(createPopUpMenu());

		// Configure the viewer
		jobsViewer = new TreeViewer(jobsTree);
		jobsViewer.setContentProvider(new JobsContentProvider());
		jobsViewer.setLabelProvider(new JobsLabelProvider());
		jobsViewer.setInput(JobManager.getDefault());
		getSite().setSelectionProvider(jobsViewer);

		// add actions
		createActions();
	}

	@Override
	// made public so that action can invoke it
	public void firePropertyChange(int id) {
		super.firePropertyChange(id);
	}

	/**
	 * Returns the JFace viewer used for the Job list.
	 * 
	 * @return the JFace viewer used for the Job list.
	 * 
	 */
	public TreeViewer getViewer() {
		return jobsViewer;
	}

	/**
	 * Pretty prints the given parameter using the graphic context of the given
	 * event.
	 * <p>
	 * This method is used to print the parameters of a job with the pattern
	 * "name:value" and to align all the values from the left.
	 * </p>
	 * 
	 * @param param
	 *            A Job parameter.
	 * @param e
	 *            An SWT event
	 */
	private void paintParam(JobParameter param, Event e) {
		// TODO make this RTL/LTR agnostic
		Integer nameLength = paramNameLength.get(param.getJob());
		if (nameLength == null) {
			nameLength = 0;
			for (JobParameter p : param.getJob().getJobParameters().values()) {
				nameLength = Math.max(nameLength, e.gc.textExtent(p
						.getScriptParameter().getNicename()).x);
			}
			nameLength += e.gc.getCharWidth(':');
			nameLength += e.gc.getCharWidth(' ');
			paramNameLength.put(param.getJob(), nameLength);
		}
		e.gc.drawText(param.getScriptParameter().getNicename() + ':', e.x, e.y);
		e.gc.drawText(param.getValue(), e.x + nameLength, e.y);

	}

	/**
	 * Passes the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		jobsViewer.getControl().setFocus();
	}
}