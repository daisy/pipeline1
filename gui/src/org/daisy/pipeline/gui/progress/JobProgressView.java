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
package org.daisy.pipeline.gui.progress;

import java.util.Arrays;
import java.util.List;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.jobs.JobsView;
import org.daisy.pipeline.gui.model.IJobChangeListener;
import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.StateManager;
import org.daisy.pipeline.gui.util.Timer;
import org.daisy.util.execution.State;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;

public class JobProgressView extends ViewPart implements ISelectionListener,
        IJobChangeListener {
    // TODO add possibility to un-synchronize
    public static final String ID = "org.daisy.pipeline.gui.views.progress"; //$NON-NLS-1$

    private ScrolledComposite control;
    private Composite noJobArea;
    private Composite jobArea;

    private Font smallerFont;
    private Label jobLabel;
    private Label iconLabel;
    private Label stateLabel;
    private StructuredViewer tasksViewer;
    private JobInfo currJobInfo;
    private ScrolledComposite scrolled;
    private ToolItem cancelButton;

    public JobProgressView() {
        StateManager.getDefault().addJobChangeListener(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayout parentLayout = new GridLayout();
        parentLayout.marginWidth = parentLayout.marginHeight = 0;
        parentLayout.verticalSpacing = 0;
        parent.setLayout(parentLayout);

        // Create the control area
        // control = new Composite(parent, SWT.NONE);
        control = new ScrolledComposite(parent, SWT.V_SCROLL);
        control.setExpandHorizontal(true);
        control.setExpandVertical(true);
        control.setLayoutData(new GridData(GridData.FILL_BOTH));
        noJobArea = createNoJobArea(control);
        jobArea = createJobArea(control);

        // Initially show no job
        showNoJob();
        getSite().setSelectionProvider(tasksViewer);
    }

    @Override
    public void dispose() {
        if (smallerFont != null) {
            smallerFont.dispose();
        }
        getSite().getPage().removePostSelectionListener(this);
        StateManager.getDefault().removeJobChangeListener(this);
        super.dispose();
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        getSite().getPage().addPostSelectionListener(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.jobs.IJobChangeListener#jobChanged(org.daisy.pipeline.gui.model.JobInfo)
     */
    public void jobChanged(final JobInfo jobInfo) {
        if (!jobInfo.equals(currJobInfo)) {
            return;
        }

        Job uiJob = new WorkbenchJob(Messages.uiJob_progressUpdate) {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                refresh();
                return Status.OK_STATUS;
            }

        };
        uiJob.setSystem(true);
        uiJob.schedule();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.jobs.IJobChangeListener#jobsChanged(java.util.List)
     */
    public void jobsChanged(List<JobInfo> jobInfos) {
        for (JobInfo jobInfo : jobInfos) {
            jobChanged(jobInfo);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (selection.isEmpty()) {
            if (JobsView.ID.equals(part.getSite().getId())) {
                showNoJob();
            }
        } else if (selection instanceof IStructuredSelection) {
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            if (obj instanceof JobInfo) {
                showJob((JobInfo) obj);
            }
        }
    }

    @Override
    public void setFocus() {
        if (currJobInfo != null && tasksViewer != null) {
            tasksViewer.getControl().setFocus();
        }
    }

    private void cancelPressed() {
        StateManager.getDefault().cancel(
                Arrays.asList(new JobInfo[] { currJobInfo }));
    }

    private Composite createJobArea(Composite parent) {
        FormData formData;
        // Creare the composite
        Composite container = new Composite(parent, SWT.NONE);
        FormLayout layout = new FormLayout();
        container.setLayout(layout);

        // Create the icon label on the left
        iconLabel = new Label(container, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(0, 10);
        formData.left = new FormAttachment(0, 10);
        iconLabel.setLayoutData(formData);

        // Create the job info
        // - toolbar
        ToolBar toolbar = new ToolBar(container, SWT.FLAT);
        formData = new FormData();
        formData.right = new FormAttachment(100, -10);
        formData.top = new FormAttachment(0, 10);
        toolbar.setLayoutData(formData);
        // - job label
        jobLabel = new Label(container, SWT.NONE);
        formData = new FormData();
        formData.left = new FormAttachment(iconLabel, 5);
        formData.right = new FormAttachment(toolbar, 5);
        formData.top = new FormAttachment(0, 10);
        jobLabel.setLayoutData(formData);
        // - state label
        stateLabel = new Label(container, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(jobLabel, 10);
        formData.left = new FormAttachment(iconLabel, 5);
        formData.right = new FormAttachment(100, -10);
        stateLabel.setLayoutData(formData);
        stateLabel.setFont(getSmallerFont(stateLabel.getFont()));

        // Create the buttons
        cancelButton = new ToolItem(toolbar, SWT.PUSH);
        cancelButton.setToolTipText(Messages.button_cancel_tooltip);
        cancelButton.setImage(GuiPlugin.getImage(IIconsKeys.ACTION_STOP));
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelPressed();
            }
        });

        // Separator
        Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
        formData = new FormData();
        formData.top = new FormAttachment(stateLabel, 15);
        formData.left = new FormAttachment(0);
        formData.right = new FormAttachment(100);
        separator.setLayoutData(formData);

        // Create the task viewer
        scrolled = new ScrolledComposite(container, SWT.V_SCROLL);
        tasksViewer = new TaskListViewer(scrolled, SWT.SINGLE);
        tasksViewer.setContentProvider(new TaskListContentProvider());
        Control taskList = tasksViewer.getControl();
        scrolled.setContent(taskList);
        scrolled.setExpandHorizontal(true);
        scrolled.setExpandVertical(true);
        formData = new FormData();
        formData.top = new FormAttachment(separator);
        formData.bottom = new FormAttachment(100);
        formData.left = new FormAttachment(0);
        formData.right = new FormAttachment(100);
        scrolled.setLayoutData(formData);
        return container;
    }

    private Composite createNoJobArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        Label label = new Label(container, SWT.NONE);
        label.setText(Messages.label_noJob);
        return container;
    }

    private Font getSmallerFont(Font font) {
        if (smallerFont == null) {
            FontData[] fd = font.getFontData();
            for (int i = 0; i < fd.length; i++) {
                fd[i].setHeight(fd[i].height - 1);
            }
            smallerFont = new Font(getSite().getShell().getDisplay(), fd);
        }
        return smallerFont;
    }

    private void refresh() {
        if (currJobInfo != null) {
            switch (currJobInfo.getSate()) {
            case ABORTED:
            case FAILED:
            case FINISHED:
                stateLabel.setText(NLS.bind(Messages.label_state_done, Timer
                        .format(currJobInfo.getTimer().getTotalTime())));
                break;
            case RUNNING:
                stateLabel.setText(NLS.bind(Messages.label_state_running, Timer
                        .format(currJobInfo.getTimer().getElapsedTime())));
                break;
            default:
                stateLabel.setText(""); //$NON-NLS-1$
                break;
            }
            jobLabel.setText(currJobInfo.getName());
            cancelButton
                    .setEnabled(currJobInfo.getSate().equals(State.RUNNING));
            ((Composite) tasksViewer.getControl()).layout();
        }
    }

    private void showJob(JobInfo jobInfo) {
        if (currJobInfo != jobInfo) {
            currJobInfo = jobInfo;
            control.setContent(jobArea);
            tasksViewer.setInput(jobInfo);
            scrolled.setMinSize(tasksViewer.getControl().computeSize(
                    SWT.DEFAULT, SWT.DEFAULT));
            refresh();
        }
    }

    private void showNoJob() {
        currJobInfo = null;
        control.setContent(noJobArea);
    }
}
