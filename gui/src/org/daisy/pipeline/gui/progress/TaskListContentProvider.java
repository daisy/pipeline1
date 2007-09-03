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
package org.daisy.pipeline.gui.progress;

import java.util.LinkedList;
import java.util.List;

import org.daisy.pipeline.gui.model.ITaskChangeListener;
import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.StateManager;
import org.daisy.pipeline.gui.model.TaskInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * The content provider for the list of tasks of a Pipeline job displayed in the
 * progress view.
 * 
 * @author Romain Deltour
 * 
 */
public class TaskListContentProvider implements IStructuredContentProvider,
		ITaskChangeListener {

	/**
	 * An Eclipse Job (i.e. Thread) that refreshes the task list.
	 */
	private class RefreshJob extends WorkbenchJob {
		/** The list of tasks to refresh */
		private List<TaskInfo> refreshInfos;

		/** Create this refresh job */
		public RefreshJob() {
			super(Messages.uiJob_taskUpdate_name);
			refreshInfos = new LinkedList<TaskInfo>();
			setSystem(true);
		}

		/**
		 * Adds the given task to the list of tasks to be refreshed.
		 * 
		 * @param info
		 *            A task to refresh
		 */
		public void add(TaskInfo info) {
			synchronized (refreshInfos) {
				refreshInfos.add(info);
			}
		}

		/**
		 * Refreshes the viewer for each task in the list.
		 */
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			synchronized (refreshInfos) {
				for (TaskInfo info : refreshInfos) {
					viewer.refresh(info);
				}
				refreshInfos.clear();
			}
			return Status.OK_STATUS;
		}
	}

	/** The Pipeline job the tasks of which are managed by this content provider */
	private JobInfo jobInfo;
	/** The Eclipse job to refresh the task list */
	private RefreshJob refreshJob;
	/** A reference to the viewer using this content provider */
	private StructuredViewer viewer;

	/**
	 * Creates the content provider and listens to the state manager.
	 * 
	 * @see StateManager
	 */
	public TaskListContentProvider() {
		refreshJob = new RefreshJob();
		StateManager.getDefault().addTaskChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		StateManager.getDefault().removeTaskChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return jobInfo.getTasks().toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if ((newInput == null) || !(newInput instanceof JobInfo)) {
			return;
		}
		jobInfo = (JobInfo) newInput;
		this.viewer = (StructuredViewer) viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.pipeline.gui.jobs.ITaskChangeListener#taskChanged(org.daisy.pipeline.gui.progress.TaskInfo)
	 */
	public void taskChanged(TaskInfo task) {
		if ((task != null) && (task.getParentJob() == jobInfo)) {
			refreshJob.add(task);
			refreshJob.schedule();
		}
	}
}
