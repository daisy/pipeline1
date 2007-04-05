package org.daisy.pipeline.gui.tasks;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import org.daisy.dmfc.core.event.BusListener;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.TransformerProgressChangeEvent;
import org.daisy.dmfc.core.event.TransformerStateChangeEvent;
import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Task;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.util.execution.State;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @author Romain Deltour
 * 
 */
public class TaskListContentProvider implements IStructuredContentProvider,
        BusListener {

    private List<TaskInfo> infos;
    private TaskInfo lastInfo;
    private Refresher refresher;

    public TaskListContentProvider() {
        infos = new ArrayList<TaskInfo>();
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
        return infos.toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput == null || !(newInput instanceof Job)) {
            return;
        }
        infos.clear();
        Job job = (Job) newInput;
        List<Task> tasks = job.getScript().getTasks();
        for (Task task : tasks) {
            infos.add(new TaskInfo(task.getTransformerInfo()));
        }
        refresher = new Refresher((StructuredViewer) viewer);
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
            Transformer trans = (Transformer) tce.getSource();
            info = getInfo(trans);
            if (info != null) {
                switch (tce.getState()) {
                case STARTED:
                    info.setState(State.RUNNING);
                    break;
                case STOPPED:
                    info.setState(State.FINISHED);
                    break;
                default:
                    break;
                }
            }
        }
        if (event instanceof TransformerProgressChangeEvent) {
            TransformerProgressChangeEvent tpce = (TransformerProgressChangeEvent) event;
            Transformer trans = (Transformer) tpce.getSource();
            info = getInfo(trans);
            if (info != null) {
                info.setProgress(tpce.getProgress());
            }
        }
        if (info != null) {
            refresher.refresh(info);
        }
    }

    private TaskInfo getInfo(Transformer trans) {
        String name = trans.getTransformerInfo().getName();
        if (lastInfo != null && lastInfo.getName().equals(name)) {
            return lastInfo;
        }
        for (TaskInfo info : infos) {
            if (info.getName().equals(name)) {
                lastInfo = info;
                return info;
            }
        }
        return null;
    }

    private class Refresher {
        private StructuredViewer viewer;
        private List<TaskInfo> refreshInfos;
        private org.eclipse.core.runtime.jobs.Job refreshJob;

        public Refresher(StructuredViewer aviewer) {
            viewer = aviewer;
            refreshInfos = new LinkedList<TaskInfo>();
            refreshJob = new WorkbenchJob("Task Progress Update Job") {
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

            };
            refreshJob.setSystem(true);
        }

        public void refresh(TaskInfo info) {
            synchronized (refreshInfos) {
                refreshInfos.add(info);
            }
            if (PlatformUI.isWorkbenchRunning()) {
                refreshJob.schedule();
            }
        }
    }
}
