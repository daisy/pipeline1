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

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.ICommandConstants;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.model.IJobChangeListener;
import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.JobManager;
import org.daisy.pipeline.gui.model.StateManager;
import org.daisy.util.execution.State;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

/**
 * The action used to clear finished jobs from the queue.
 * 
 * @author Romain Deltour
 * 
 */
public class ClearFinishedAction extends Action implements IJobChangeListener {
	/**
	 * The underlying undoable operation that do clear the jobs.
	 */
	protected class ClearFinishedOperation extends AbstractOperation {

		private IStructuredSelection sel;
		private SortedMap<Integer, JobInfo> map;

		/**
		 * Creates the operation and initializes it with finished jobs.
		 */
		public ClearFinishedOperation() {
			super(getText());
			map = new TreeMap<Integer, JobInfo>();
			List<JobInfo> finishedJobs = jobManager
					.getJobsByState(finishedStates);
			for (JobInfo jobInfo : finishedJobs) {
				map.put(jobManager.indexOf(jobInfo), jobInfo);
			}
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			return redo(monitor, info);
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			jobManager.removeAll(map.values());
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			for (Integer index : map.keySet()) {
				jobManager.add(index, map.get(index));
			}
			view.getViewer().setSelection(sel);
			return Status.OK_STATUS;
		}

	}

	/** The enumeration of states of finished jobs */
	private static final EnumSet<State> finishedStates = EnumSet.of(
			State.ABORTED, State.FAILED, State.FINISHED);

	/** A reference to the jobs view */
	private final JobsView view;
	/** A reference to the jobs manager */
	private final JobManager jobManager;

	/**
	 * Creates the action for the given jobs view.
	 * 
	 * @param view
	 *            the jobs view this action applies to.
	 */
	public ClearFinishedAction(JobsView view) {
		super(Messages.action_clearFinished, GuiPlugin
				.createDescriptor(IIconsKeys.CLEAR_FINISHED));
		this.view = view;
		this.jobManager = JobManager.getDefault();
		setActionDefinitionId(ICommandConstants.CLEAR_FINISHED_CMD);
		setEnabled(false);
		StateManager.getDefault().addJobChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.pipeline.gui.model.IJobChangeListener#jobChanged(org.daisy.pipeline.gui.model.JobInfo)
	 */
	public void jobChanged(JobInfo jobInfo) {
		jobsChanged(Arrays.asList(new JobInfo[] { jobInfo }));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.pipeline.gui.model.IJobChangeListener#jobsChanged(java.util.List)
	 */
	public void jobsChanged(List<JobInfo> jobInfos) {
		for (JobInfo info : jobInfos) {
			if (finishedStates.contains(info.getSate())) {
				setEnabled(true);
				return;
			}
		}
		refreshEnable();
	}

	private void refreshEnable() {
		setEnabled(jobManager.getJobsByState(finishedStates).size() > 0);
	}

	@Override
	public void run() {
		IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		IUndoContext undoContext = PlatformUI.getWorkbench()
				.getOperationSupport().getUndoContext();
		IUndoableOperation operation = new ClearFinishedOperation();
		operation.addContext(undoContext);
		try {
			// No need to provide monitor or GUI context
			operationHistory.execute(operation, null, null);
		} catch (ExecutionException e) {
			// TODO implement better exception dialog
			MessageDialog.openError(view.getSite().getShell(),
					Messages.error_delete_title, Messages.error_delete_message
							+ e.getMessage());
		}
	}
}
