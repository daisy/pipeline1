package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.pipeline.gui.tasks.TaskListContentProvider;
import org.daisy.pipeline.gui.tasks.TaskListViewer;
import org.daisy.util.execution.State;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;

public class JobDetailsView extends ViewPart implements ISelectionListener,
        IJobChangeListener {
    // TODO add possibility to un-synchronize
    public static final String ID = "org.daisy.pipeline.gui.views.jobDetails";

    private StructuredViewer viewer;
    private Label label;

    public JobDetailsView() {
        StateManager.getDefault().addJobChangeListener(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayout layout = new GridLayout(1, true);
        parent.setLayout(layout);
        label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        viewer = new TaskListViewer(parent, SWT.SINGLE);
        viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        viewer.setContentProvider(new TaskListContentProvider());
        getSite().setSelectionProvider(viewer);
    }

    @Override
    public void dispose() {
        getSite().getPage().removePostSelectionListener(this);
        StateManager.getDefault().removeJobChangeListener(this);
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
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            if (obj instanceof JobInfo) {
                label.setText(((JobInfo) obj).getName());
                viewer.setInput(obj);
                // TODO refresh the viewer layout
                // viewer.refresh();
                ((Composite) viewer.getControl()).layout();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.jobs.IJobChangeListener#jobChanged(org.daisy.pipeline.gui.jobs.model.JobInfo)
     */
    public void jobChanged(final JobInfo job) {
        if (!viewer.getInput().equals(job) && job.getSate() == State.RUNNING) {

            // TODO set ui job family
            org.eclipse.core.runtime.jobs.Job uiJob = new WorkbenchJob(
                    "Task Detail Update Job") {
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    viewer.setInput(job);
                    return Status.OK_STATUS;
                }

            };
            uiJob.setSystem(true);
            uiJob.schedule();
        }

    }
}
