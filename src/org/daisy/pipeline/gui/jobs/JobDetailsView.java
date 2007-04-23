package org.daisy.pipeline.gui.jobs;

import java.util.EventObject;

import org.daisy.dmfc.core.event.BusListener;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.ScriptStateChangeEvent;
import org.daisy.dmfc.core.event.StateChangeEvent;
import org.daisy.dmfc.core.script.Script;
import org.daisy.dmfc.core.script.Job;
import org.daisy.pipeline.gui.tasks.TaskListContentProvider;
import org.daisy.pipeline.gui.tasks.TaskListViewer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;

public class JobDetailsView extends ViewPart implements ISelectionListener,
        BusListener {
    // TODO add possibility to un-synchronize
    public static final String ID = "org.daisy.pipeline.gui.views.jobDetails";

    private StructuredViewer viewer;

    public JobDetailsView() {
        EventBus.getInstance().subscribe(this, ScriptStateChangeEvent.class);
    }

    @Override
    public void createPartControl(Composite parent) {
        viewer = new TaskListViewer(parent, SWT.SINGLE);
        viewer.setContentProvider(new TaskListContentProvider());
        // TODO init input from viewer
        getSite().setSelectionProvider(viewer);
    }

    @Override
    public void dispose() {
        getSite().getPage().removePostSelectionListener(this);
        super.dispose();
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        getSite().getPage().addPostSelectionListener(this);
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.dmfc.core.event.BusListener#recieved(java.util.EventObject)
     */
    public void recieved(EventObject event) {
        if (event instanceof ScriptStateChangeEvent) {
            ScriptStateChangeEvent ssce = (ScriptStateChangeEvent) event;
            final Script script = (Script) ssce.getSource();
            if (ssce.getState() == StateChangeEvent.Status.STARTED) {
                // TODO set ui job family
                org.eclipse.core.runtime.jobs.Job uiJob = new WorkbenchJob(
                        "Task Detail Update Job") {
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        viewer.setInput(script);
                        return Status.OK_STATUS;
                    }

                };
                uiJob.setSystem(true);
                uiJob.schedule();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            if (obj instanceof Job) {
                Job job = (Job) obj;
                viewer.setInput(job.getScript());
                // TODO refresh the viewer layout
                // viewer.refresh();
                ((Composite) viewer.getControl()).layout();
            }
        }
    }
}
