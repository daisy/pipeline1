package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.jobs.model.IJobManagerListener;
import org.daisy.pipeline.gui.jobs.model.Job;
import org.daisy.pipeline.gui.jobs.model.JobManager;
import org.daisy.pipeline.gui.jobs.model.JobManagerEvent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

public class JobsContentProvider implements IStructuredContentProvider,
        IJobManagerListener {
    private TableViewer viewer;
    private JobManager manager;

    public Object[] getElements(Object inputElement) {
        return manager.toArray();
    }

    public void dispose() {
        if (manager != null) {
            manager.removeJobsManagerListener(this);
        }
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (TableViewer) viewer;
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
                viewer.add(event.getJobs());
            } else {
                for (Job job : event.getJobs()) {
                    viewer.insert(job,index++);
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

}
