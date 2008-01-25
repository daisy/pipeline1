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

import org.daisy.pipeline.core.script.JobParameter;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.jobs.JobsView;
import org.daisy.pipeline.gui.model.IJobChangeListener;
import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.JobManager;
import org.daisy.pipeline.gui.model.StateManager;
import org.daisy.pipeline.gui.util.Timer;
import org.daisy.util.execution.State;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * The implementation of the Progress View.
 * 
 * @author Romain Deltour
 * 
 */
public class JobProgressView extends ViewPart implements ISelectionListener,
		IJobChangeListener {
	/** The ID of this view (as used in the plugin.xml file) */
	public static final String ID = "org.daisy.pipeline.gui.views.progress"; //$NON-NLS-1$
	/**
	 * The top-level composite of this view (it's a scrolled composite so that
	 * we can easily switch its content
	 */
	private ScrolledComposite control;
	/** The empty composite to display when no job is selected */
	private Composite noJobArea;
	/** The parent composite to display when a job is selected */
	private Composite jobArea;
	/**
	 * The label presenting the title of the currently displayed job. Note: this
	 * is a read-only Text for accessibility purpose
	 */
	private Text jobLabel;
	/** The icon presenting the status of the currently displayed job */
	private Label iconLabel;
	/** The label presenting the status of the currently displayed job */
	private Label stateLabel;
	/** The JFace viewer used for the task list */
	private StructuredViewer tasksViewer;
	/** The currently displayed Job */
	private JobInfo currJobInfo;
	/** The button for canceling the currently displayed job */
	private ToolItem cancelButton;

	/**
	 * Creates this view and adds itself as a listener to the StateManager.
	 * 
	 * @see StateManager
	 */
	public JobProgressView() {
		StateManager.getDefault().addJobChangeListener(this);
	}

	/**
	 * Called when the user selects the cancel button. This method cancels the
	 * currently displayed job via the StateManager.
	 * 
	 * @see StateManager
	 */
	private void cancelPressed() {
		StateManager.getDefault().cancel(
				Arrays.asList(new JobInfo[] { currJobInfo }));
	}

	/**
	 * Creates the content of the view for displaying the progress info of the
	 * currently selected job.
	 * 
	 * @param parent
	 *            The parent of the created widgets
	 * @return The embedding Composite.
	 */
	private Composite createJobArea(Composite parent) {
		FormData formData;
		// Create the composite
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
		jobLabel = new Text(container, SWT.READ_ONLY);
		jobLabel.setBackground(jobLabel.getDisplay().getSystemColor(
				SWT.COLOR_WIDGET_BACKGROUND));
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
		// Create the cancel buttons
		cancelButton = new ToolItem(toolbar, SWT.PUSH);
		cancelButton.setToolTipText(IDialogConstants.CANCEL_LABEL);
		cancelButton.setImage(GuiPlugin.getImage(IIconsKeys.ACTION_STOP));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelPressed();
			}
		});

		// Hack for Mac OS X background color
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			// TODO remove when Eclipse bug #204915 is fixed
			Color bgcolor = container.getDisplay().getSystemColor(
					SWT.COLOR_WIDGET_BACKGROUND);
			container.setBackground(bgcolor);
			iconLabel.setBackground(bgcolor);
			stateLabel.setBackground(bgcolor);
			toolbar.setBackground(bgcolor);
		}

		// Add accessibility to Job label
		jobLabel.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getName(AccessibleEvent e) {
				StringBuilder sb = new StringBuilder();
				sb.append(jobLabel.getText()).append(' ');
				sb.append(stateLabel.getText());
				e.result = sb.toString();
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
		tasksViewer = new TaskListViewer(container, SWT.SINGLE | SWT.V_SCROLL);
		tasksViewer.setContentProvider(new TaskListContentProvider());
		Control taskList = tasksViewer.getControl();
		formData = new FormData();
		formData.top = new FormAttachment(separator);
		formData.bottom = new FormAttachment(100);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		taskList.setLayoutData(formData);
		container.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				System.err.println(e.widget);
			}
		});
		return container;
	}

	/**
	 * Create the content of the view when no job is selected.
	 * 
	 * @param parent
	 *            The top-level composite of the view
	 * @return The created parent composite
	 */
	private Composite createNoJobArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		Text text = new Text(container, SWT.READ_ONLY);
		text.setText(Messages.label_noJob);
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			// TODO remove when Eclipse bug #204915 is fixed
			container.setBackground(text.getDisplay().getSystemColor(
					SWT.COLOR_WIDGET_BACKGROUND));
			text.setBackground(text.getDisplay().getSystemColor(
					SWT.COLOR_WIDGET_BACKGROUND));
		}
		return container;
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
		getSite().getPage().removeSelectionListener(this);
		StateManager.getDefault().removeJobChangeListener(this);
		super.dispose();
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		getSite().getPage().addSelectionListener(this);
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

	/**
	 * Refreshes the view content with the latest state of the currently
	 * selected Job.
	 */
	private void refresh() {
		if (currJobInfo != null) {
			State state = currJobInfo.getSate();
			switch (state) {
			case ABORTED:
				stateLabel.setText(NLS.bind(Messages.label_state_aborted, Timer
						.format(currJobInfo.getTimer().getTotalTime())));
				break;
			case FAILED:
				stateLabel.setText(NLS.bind(Messages.label_state_failed, Timer
						.format(currJobInfo.getTimer().getTotalTime())));
				break;
			case FINISHED:
				stateLabel.setText(NLS.bind(Messages.label_state_done, Timer
						.format(currJobInfo.getTimer().getTotalTime())));
				break;
			case RUNNING:
				stateLabel.setText(NLS.bind(Messages.label_state_running, Timer
						.format(currJobInfo.getTimer().getElapsedTime())));
				break;
			case IDLE:
			case WAITING:
			default:
				stateLabel.setText(state.toString());
				break;
			}
			jobLabel.setText(currJobInfo.getName());
			cancelButton
					.setEnabled(currJobInfo.getSate().equals(State.RUNNING));
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
			if (JobsView.ID.equals(part.getSite().getId())
					&& (JobManager.getDefault().indexOf(currJobInfo) == -1)) {
				showNoJob();
			}
		} else if (selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof JobInfo) {
				showJob((JobInfo) obj);
			}
			if (obj instanceof JobParameter) {
				showJob(JobManager.getDefault().get(
						((JobParameter) obj).getJob()));
			}
		}
	}

	@Override
	public void setFocus() {
		if (currJobInfo != null) {
			jobLabel.setFocus();
		} else {
			noJobArea.setFocus();
		}
	}

	/**
	 * Displays the given job in this view.
	 * 
	 * @param jobInfo
	 *            The Pipeline Job to display in this view
	 */
	private void showJob(JobInfo jobInfo) {
		if (currJobInfo != jobInfo) {
			currJobInfo = jobInfo;
			control.setContent(jobArea);
			tasksViewer.setInput(jobInfo);
			refresh();
		}
	}

	/**
	 * Set this view do display no job, i.e. to display an empty area.
	 */
	private void showNoJob() {
		currJobInfo = null;
		control.setContent(noJobArea);
	}
}
