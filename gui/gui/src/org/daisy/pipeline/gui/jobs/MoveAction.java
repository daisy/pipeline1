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
package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.JobManager;
import org.daisy.pipeline.gui.util.actions.OperationUtil;
import org.daisy.pipeline.gui.util.viewers.DefaultSelectionEnabler;
import org.daisy.pipeline.gui.util.viewers.ISelectionEnabler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IPropertyListener;

/**
 * Abstract base class for the actions that move jobs in the the jobs view.
 * 
 * @author Romain Deltour
 * 
 */
public abstract class MoveAction extends Action implements IPropertyListener {
	/**
	 * An undoable operation that is used internally by move actions. This
	 * operation saves the selection in the view.
	 * 
	 * @author Romain Deltour
	 * 
	 */
	protected class MoveOperation extends AbstractOperation {

		private final int oldIndex;
		private final int newIndex;
		private final ISelection sel;

		/**
		 * Creates a new operation representing a job move in the jobs view.
		 * 
		 * @param oldIndex
		 *            the old index of the job that is moved
		 * @param newIndex
		 *            the new index of the job that is moved
		 * @param sel
		 *            the selection in the view before the move
		 */
		public MoveOperation(int oldIndex, int newIndex, ISelection sel) {
			super(getText());
			this.oldIndex = oldIndex;
			this.newIndex = newIndex;
			this.sel = sel;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			redo(monitor, info);
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			jobManager.move(oldIndex, newIndex);
			view.firePropertyChange(JobsView.PROP_SEL_JOB_INDEX);
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			jobManager.move(newIndex, oldIndex);
			view.firePropertyChange(JobsView.PROP_SEL_JOB_INDEX);
			view.getViewer().setSelection(sel);
			return Status.OK_STATUS;
		}

	}

	/** A reference to the Jobs view this action is used in */
	protected final JobsView view;
	/** The currently selected element */
	protected Object selectedElem;
	/** The current selection */
	protected ISelection selection;
	/** A reference to the pipeline job manager */
	protected JobManager jobManager;
	/** The object that controls the enablement state of this aciton */
	private ISelectionEnabler enabler;

	/**
	 * Creates a new move action.
	 * 
	 * @param view
	 *            the jobs view this action is used with
	 * @param name
	 *            the name of this action
	 * @param icon
	 *            the icon used to represent this action
	 */
	public MoveAction(JobsView view, String name, ImageDescriptor icon) {
		super(name, icon);
		this.view = view;
		this.jobManager = JobManager.getDefault();
		this.enabler = new DefaultSelectionEnabler(
				ISelectionEnabler.Mode.SINGLE, new Class[] { JobInfo.class });
		setEnabled(false);
		this.view.addPropertyListener(this);
	}

	/**
	 * Returns the undoable operation used to execute this action.
	 * 
	 * @return the underlying undoable operation used by this action
	 */
	protected abstract IUndoableOperation getOperation();

	public void propertyChanged(Object source, int propId) {
		if (propId != JobsView.PROP_SEL_JOB_INDEX) {
			return;
		}
		ISelection incoming = view.getViewer().getSelection();
		setEnabled(enabler.isEnabledFor(incoming));
		selection = incoming;
		selectedElem = ((IStructuredSelection) incoming).getFirstElement();
	}

	@Override
	public void run() {
		OperationUtil.execute(getOperation(), view.getSite().getShell());
	}
}
