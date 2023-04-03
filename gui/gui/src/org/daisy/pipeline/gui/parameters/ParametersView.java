/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.parameters;

import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.JobParameter;
import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.JobManager;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * An experimental view for showing / editing the parameters of a Job.
 * 
 * @author Romain Deltour
 * 
 */
public class ParametersView extends ViewPart implements ISelectionListener {
	/** The ID of this view as specified in the plugin.xml */
	public static final String ID = "org.daisy.pipeline.gui.views.parameters"; //$NON-NLS-1$

	private static final String[] columnNames = { Messages.heading_name,
			Messages.heading_value };
	private static final int[] columnWeight = { 2, 3 };

	private TreeViewer viewer;

	// private List<FilterToggleAction> filterToggleActions;

	@Override
	public void createPartControl(Composite parent) {
		// Create the tree
		Tree paramsTree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI | SWT.FULL_SELECTION);
		paramsTree.setHeaderVisible(true);
		paramsTree.setLinesVisible(true);

		// Configure the columns
		TableLayout layout = new TableLayout();
		paramsTree.setLayout(layout);
		for (int i = 0; i < columnNames.length; i++) {
			layout.addColumnData(new ColumnWeightData(columnWeight[i], true));
			TreeColumn tc = new TreeColumn(paramsTree, SWT.NONE, i);
			tc.setText(columnNames[i]);
			tc.setMoveable(true);
		}

		// Create the viewer
		viewer = new TreeViewer(paramsTree);
		viewer.setContentProvider(new ParamsContentProvider());
		viewer.setLabelProvider(new ParamsLabelProvider());
		getSite().setSelectionProvider(viewer);

		// Create filter
		// filter = new ParamsFilter();
		// viewer.addFilter(filter);

		// Create actions
		createActions();
	}

	private void createActions() {
		// IAction filterDialogAction = new FilterDialogAction();
		// filterToggleActions = new LinkedList<FilterToggleAction>();
		// for (MessageEvent.Type type : MessageEvent.Type.values()) {
		// filterToggleActions.add(new FilterToggleAction(type));
		// }
		// for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
		// filterToggleActions.add(new FilterToggleAction(cause));
		// }
		//
		// // Configure the drop down menu
		// IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		// menu.add(filterDialogAction);
		// IMenuManager groupByMenu = new MenuManager("Group By");
		// menu.add(groupByMenu);
		// Map<String, List<Category>> catMap = createCategories();
		// for (String key : catMap.keySet()) {
		// groupByMenu.add(new GroupByAction(key, viewer, catMap.get(key)));
		// }
		// // groupByMenu.add(action);
		//
		// // Configure the tool bar
		// IToolBarManager toolbar = getViewSite().getActionBars()
		// .getToolBarManager();
		// for (FilterToggleAction action : filterToggleActions) {
		// toolbar.add(action);
		// }
	}

	// private Map<String, List<Category>> createCategories() {
	// Map<String, List<Category>> map = new LinkedHashMap<String,
	// List<Category>>();
	// // Severity category
	// List<Category> typeCat = new LinkedList<Category>();
	// for (MessageEvent.Type type : MessageEvent.Type.values()) {
	// typeCat.add(new CategoryType(type));
	// }
	// map.put("Severity", typeCat);
	// // Type category
	// List<Category> causeCat = new LinkedList<Category>();
	// for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
	// causeCat.add(new CategoryCause(cause));
	// }
	// map.put("Type", causeCat);
	// // No category
	// map.put("None", null);
	// return map;
	// }

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!selection.isEmpty() && (selection instanceof IStructuredSelection)) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof JobInfo) {
				viewer.setInput(obj);
			}
			if (obj instanceof JobParameter) {
				Job job = ((JobParameter) obj).getJob();
				if (job != ((JobInfo) viewer.getInput()).getJob()) {
					viewer.setInput(JobManager.getDefault().get(job));
				}
			}
		}

	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		getSite().getPage().addPostSelectionListener(this);
	}

	@Override
	public void dispose() {
		getSite().getPage().removePostSelectionListener(this);
		super.dispose();
	}

	// private class FilterDialogAction extends Action {
	//
	// public FilterDialogAction() {
	// super("Filter...");
	// }
	//
	// @Override
	// public void run() {
	// FilterDialog dialog = new FilterDialog(getSite().getShell(), filter);
	//
	// if (dialog.open() == IDialogConstants.OK_ID) {
	// viewer.refresh();
	// for (FilterToggleAction action : filterToggleActions) {
	// action.refresh();
	// }
	// }
	// }
	//
	// }

	// private class FilterToggleAction extends Action {
	//
	// private boolean checked;
	// private MessageEvent.Cause cause;
	// private MessageEvent.Type type;
	//
	// public FilterToggleAction(MessageEvent.Cause cause) {
	// // TODO localize
	// super(cause.toString(), IAction.AS_CHECK_BOX);
	// this.cause = cause;
	// refresh();
	// }
	//
	// public FilterToggleAction(MessageEvent.Type type) {
	// // TODO localize
	// super(type.toString(), IAction.AS_CHECK_BOX);
	// this.type = type;
	// refresh();
	// }
	//
	// @Override
	// public void run() {
	// checked = !checked;
	// if (cause != null) {
	// filter.configure(cause, checked);
	// } else if (type != null) {
	// filter.configure(type, checked);
	// }
	// viewer.refresh();
	// }
	//
	// public void refresh() {
	// if (cause != null) {
	// setChecked(filter.isAccepted(cause));
	// } else if (type != null) {
	// setChecked(filter.isAccepted(type));
	// }
	// this.checked = isChecked();
	// }
	// }

	// private class CategoryCause extends Category {
	//
	// private MessageEvent.Cause cause;
	//
	// public CategoryCause(MessageEvent.Cause cause) {
	// // TODO localize
	// super(cause.toString());
	// this.cause = cause;
	// }
	//
	// @Override
	// public boolean contains(Object obj) {
	// if (obj instanceof MessageEvent) {
	// return ((MessageEvent) obj).getCause() == cause;
	// }
	// return false;
	// }
	//
	// }

	// private class CategoryType extends Category {
	//
	// private MessageEvent.Type type;
	//
	// public CategoryType(MessageEvent.Type type) {
	// // TODO localize
	// super(type.toString());
	// this.type = type;
	// }
	//
	// @Override
	// public boolean contains(Object obj) {
	// if (obj instanceof MessageEvent) {
	// return ((MessageEvent) obj).getType() == type;
	// }
	// return false;
	// }
	//
	// }
}