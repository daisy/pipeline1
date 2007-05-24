package org.daisy.pipeline.gui.jobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.daisy.dmfc.core.script.JobParameter;
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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.progress.WorkbenchJob;

public class JobsContentProvider implements ITreeContentProvider,
        IJobManagerListener, IJobChangeListener {
    private TreeViewer viewer;
    private JobManager manager;

    public JobsContentProvider() {
        StateManager.getDefault().addJobChangeListener(this);
    }

    public Object[] getElements(Object inputElement) {
        return manager.toArray();
    }

    public void dispose() {
        if (manager != null) {
            manager.removeJobsManagerListener(this);
        }
        StateManager.getDefault().removeJobChangeListener(this);
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
            break;
        case REMOVE:
            viewer.remove(event.getJobs());
            break;
        case UPDATE:
            viewer.refresh();
            break;
        default: // should never happen
            viewer.refresh();
            break;
        }
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
                if (param.getValue() != null && param.getValue().length() > 0) {
                    valuedParams.add(param);
                }
            }
            return valuedParams.toArray();
        }
        return new Object[0];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        if (element instanceof JobParameter) {
            return ((JobParameter) element).getJob();
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

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.jobs.IJobChangeListener#jobChanged(org.daisy.pipeline.gui.model.JobInfo)
     */
    public void jobChanged(final JobInfo jobInfo) {
        new RefreshUIJob(Arrays.asList(new JobInfo[] { jobInfo })).schedule();
    }

    public void jobsChanged(final List<JobInfo> jobInfos) {
        new RefreshUIJob(jobInfos).schedule();
    }

    private class RefreshUIJob extends WorkbenchJob {
        private List<JobInfo> jobInfos;

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
}
