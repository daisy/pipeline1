package org.daisy.pipeline.gui.tasks;

import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import org.daisy.dmfc.core.event.BusListener;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.TransformerProgressChangeEvent;
import org.daisy.dmfc.core.event.TransformerStateChangeEvent;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.util.execution.State;
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
        BusListener {

    private JobInfo jobInfo;
    private TaskInfo lastInfo;
    private boolean inputChanged;
    private RefreshJob refreshJob;
    private StructuredViewer viewer;

    public TaskListContentProvider() {
        refreshJob = new RefreshJob();
        EventBus.getInstance().subscribe(this,
                TransformerProgressChangeEvent.class);
        EventBus.getInstance().subscribe(this,
                TransformerStateChangeEvent.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        EventBus.getInstance().unsubscribe(this,
                TransformerProgressChangeEvent.class);
        EventBus.getInstance().unsubscribe(this,
                TransformerStateChangeEvent.class);
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
        inputChanged = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.dmfc.core.event.BusListener#recieved(java.util.EventObject)
     */
    public void recieved(EventObject event) {
        TaskInfo info = null;
        if (event instanceof TransformerStateChangeEvent) {
            TransformerStateChangeEvent tce = (TransformerStateChangeEvent) event;
            info = getInfo((Transformer) tce.getSource());
        }
        if (event instanceof TransformerProgressChangeEvent) {
            TransformerProgressChangeEvent tpce = (TransformerProgressChangeEvent) event;
            info = getInfo((Transformer) tpce.getSource());
        }
        if (info != null) {
            refreshJob.add(info);
            refreshJob.schedule();
        }
    }

    private TaskInfo getInfo(Transformer trans) {
        String name = trans.getTransformerInfo().getName();
        if (!inputChanged && lastInfo != null
                && lastInfo.getName().equals(name)) {
            return lastInfo;
        }
        for (TaskInfo info : jobInfo.getTasks()) {
            if (info.getName().equals(name)) {
                lastInfo = info;
                return info;
            }
        }
        return null;
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
}
