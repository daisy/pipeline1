package org.daisy.pipeline.gui.tasks;

import java.util.LinkedList;
import java.util.List;

import org.daisy.pipeline.gui.jobs.ITaskChangeListener;
import org.daisy.pipeline.gui.jobs.StateManager;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @author Romain Deltour
 * 
 */
public class TaskListContentProvider implements IStructuredContentProvider,
        ITaskChangeListener {

    private JobInfo jobInfo;
    private RefreshJob refreshJob;
    private StructuredViewer viewer;

    public TaskListContentProvider() {
        refreshJob = new RefreshJob();
        StateManager.getInstance().addTaskChangeListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        StateManager.getInstance().removeTaskChangeListener(this);
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
        if (newInput == null || !(newInput instanceof JobInfo)) {
            return;
        }
        jobInfo = (JobInfo) newInput;
        this.viewer = (StructuredViewer) viewer;
    }

    private class RefreshJob extends WorkbenchJob {
        TaskInfo info;
        private List<TaskInfo> refreshInfos;

        public RefreshJob() {
            super("Task Refresh Job");
            refreshInfos = new LinkedList<TaskInfo>();
            setSystem(true);
        }

        public void add(TaskInfo info) {
            synchronized (refreshInfos) {
                refreshInfos.add(info);
            }
        }

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

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.jobs.ITaskChangeListener#taskChanged(org.daisy.pipeline.gui.tasks.TaskInfo)
     */
    public void taskChanged(TaskInfo task) {
        if (task != null && task.getParentJob() == jobInfo) {
            refreshJob.add(task);
            refreshJob.schedule();
        }
    }
}
