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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.daisy.pipeline.core.script.JobParameter;
import org.daisy.pipeline.gui.model.IJobChangeListener;
import org.daisy.pipeline.gui.model.IJobManagerListener;
import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.JobManager;
import org.daisy.pipeline.gui.model.JobManagerEvent;
import org.daisy.pipeline.gui.model.StateManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * The content provider of the Jobs queue. Maps the content of the
 * {@link JobManager} and provides job parameters as children of jobs tree
 * nodes.
 * 
 * @author Romain Deltour
 * 
 */
public class JobsContentProvider implements ITreeContentProvider,
		IJobManagerListener, IJobChangeListener {
	/**
	 * A runtime job to refresh the UI state when a set of jobs changed
	 */
	private class RefreshUIJob extends WorkbenchJob {
		private List<JobInfo> jobInfos;

		/**
		 * Create a refresh runtime job to update the UI for the given pipeline
		 * jobs
		 * 
		 * @param jobInfos
		 *            A list of Pipeline jobs
		 */
		public RefreshUIJob(List<JobInfo> jobInfos) {
			super(Messages.uiJob_updateJobs);
			this.jobInfos = jobInfos;
			setSystem(true);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			try {
				monitor.beginTask(Messages.uiJob_updateJobs_task, jobInfos
						.size());
				for (JobInfo jobInfo : jobInfos) {
					monitor.subTask(NLS.bind(Messages.uiJob_updateJobs_subtask,
							jobInfo.getName()));
					viewer.refresh(jobInfo);
					monitor.worked(1);
				}
			} finally {
				monitor.done();
			}
			return Status.OK_STATUS;
		}
	}

	/** The tree viewer for the jobs queue */
	private TreeViewer viewer;
	/** A reference to the job manager */
	private JobManager manager;

	/**
	 * Creates the content provider and adds it as a listener to the
	 * {@link StateManager}.
	 */
	public JobsContentProvider() {
		StateManager.getDefault().addJobChangeListener(this);
	}

	public void dispose() {
		if (manager != null) {
			manager.removeJobsManagerListener(this);
		}
		StateManager.getDefault().removeJobChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof JobInfo) {
			JobInfo info = (JobInfo) parentElement;
			Collection<JobParameter> params = info.getJob().getJobParameters()
					.values();
			List<JobParameter> valuedParams = new ArrayList<JobParameter>(
					params.size());
			for (JobParameter param : params) {
				if ((param.getValue() != null)
						&& (param.getValue().length() > 0)) {
					valuedParams.add(param);
				}
			}
			return valuedParams.toArray();
		}
		return new Object[0];
	}

	public Object[] getElements(Object inputElement) {
		return manager.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
		if (element instanceof JobParameter) {
			return manager.get(((JobParameter) element).getJob());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;
		if (manager != null) {
			manager.removeJobsManagerListener(this);
		}
		manager = (JobManager) newInput;
		if (manager != null) {
			manager.addJobsManagerListener(this);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.pipeline.gui.jobs.IJobChangeListener#jobChanged(org.daisy.pipeline.gui.model.JobInfo)
	 */
	public void jobChanged(final JobInfo jobInfo) {
		new RefreshUIJob(Arrays.asList(new JobInfo[] { jobInfo })).schedule();
	}

	public void jobManagerChanged(JobManagerEvent event) {
		switch (event.getType()) {
		case ADD:
			int index = event.getIndex();
			if (index == -1) {
				viewer.add(manager, event.getJobs());
			} else {
				for (JobInfo job : event.getJobs()) {
					viewer.insert(manager, job, index++);
				}
			}
			viewer.setSelection(new StructuredSelection(event.getJobs()));
			break;
		case REMOVE:
			boolean selWasEmpty = viewer.getSelection().isEmpty();
			// Do remove the job
			viewer.remove(event.getJobs());
			// Reset the selection if the selection was removed
			if (viewer.getSelection().isEmpty() && !selWasEmpty
					&& !manager.isEmpty()) {
				viewer.setSelection(new StructuredSelection(manager.get((event
						.getIndex() > 0) ? event.getIndex() - 1 : 0)));
			}
			break;
		case UPDATE:
			viewer.refresh();
			break;
		default: // should never happen
			viewer.refresh();
			break;
		}
	}

	public void jobsChanged(final List<JobInfo> jobInfos) {
		new RefreshUIJob(jobInfos).schedule();
	}
}