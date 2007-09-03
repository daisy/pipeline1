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

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.model.TaskInfo;
import org.daisy.pipeline.gui.util.Timer;
import org.daisy.pipeline.gui.util.swt.CompositeItem;
import org.daisy.pipeline.gui.util.swt.CompositeList;
import org.daisy.util.execution.State;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * An item of a {@link CompositeList} as used in a {@link TaskListViewer}.
 * <p>
 * This custom composite is used to display the progress of a task within a
 * Pipeline Job. It basically consists in the name of the tasks, its position in
 * the task list, and has a progress bar to show the progress information.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public class TaskItem extends CompositeItem {

	/**
	 * Returns the image icon representing the given state.
	 * 
	 * @param state
	 *            A progress state
	 * @return The image icon representing the given state.
	 */
	private static Image getImage(State state) {
		Image image = null;
		switch (state) {
		case ABORTED:
			return GuiPlugin.getImage(IIconsKeys.STATE_CANCELED);
		case FAILED:
			return GuiPlugin.getImage(IIconsKeys.STATE_FAILED);
		case FINISHED:
			return GuiPlugin.getImage(IIconsKeys.STATE_FINISHED);
		case IDLE:
			return GuiPlugin.getImage(IIconsKeys.STATE_IDLE);
		case RUNNING:
			return GuiPlugin.getImage(IIconsKeys.STATE_RUNNING);
		case WAITING:
			return GuiPlugin.getImage(IIconsKeys.STATE_WAITING);
		}
		return image;
	}

	/**
	 * Returns the textual timing information for given task.
	 * 
	 * @param info
	 *            A Pipeline task
	 * @return the timing information on the given task.
	 */
	private static String getTimeText(TaskInfo info) {
		String text;
		switch (info.getSate()) {
		case RUNNING:
			text = NLS.bind(Messages.state_timeRunning, Timer.format(info
					.getTimer().getLeftTime()));
			break;
		case FINISHED:
			text = NLS.bind(Messages.state_timeDone, Timer.format(info
					.getTimer().getTotalTime()));
			break;
		default:
			text = ""; //$NON-NLS-1$
			break;
		}
		return text;
	}

	/** The Pipeline task represented by this widget */
	private TaskInfo taskInfo;
	/** The label containing the status icon */
	private Label iconLabel;
	/** The label presenting the name of the task */
	private Label nameLabel;
	/** The label presenting the position of the task in the job */
	private Label numLabel;
	/** The label presenting the timing information */
	private Label timeLabel;

	/** The progress bar showing the execution state */
	private ProgressBar progressBar;

	/** The state of this task at the last refresh */
	private State lastState;

	/**
	 * Simply calls the parent constructor.
	 * 
	 * @param parent
	 *            The <code>CompositeList</code> parent of this item.
	 * @param style
	 *            The {@link SWT} style of this item.
	 * @param index
	 *            The index of this item in its parent list.
	 */
	public TaskItem(CompositeList<TaskItem> parent, int style, int index) {
		super(parent, style, index);
	}

	@Override
	protected void createChildren() {
		FormData formData;
		FormLayout layout = new FormLayout();
		setLayout(layout);

		// Create the icon label on the left
		iconLabel = new Label(this, SWT.NONE);
		formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(0, 5);
		iconLabel.setLayoutData(formData);

		// Create the numbering label
		numLabel = new Label(this, SWT.NONE);
		formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -5);
		numLabel.setLayoutData(formData);

		// Create name label
		nameLabel = new Label(this, SWT.NONE);
		formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(iconLabel, 5);
		formData.right = new FormAttachment(numLabel, -5);
		nameLabel.setLayoutData(formData);

		// Create time info label
		timeLabel = new Label(this, SWT.NONE);
		formData = new FormData();
		formData.top = new FormAttachment(nameLabel, 5);
		formData.left = new FormAttachment(iconLabel, 5);
		formData.right = new FormAttachment(100, -5);
		timeLabel.setLayoutData(formData);

		// Create Progress Bar
		progressBar = new ProgressBar(this, SWT.HORIZONTAL);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		formData = new FormData();
		formData.top = new FormAttachment(timeLabel, 5);
		formData.left = new FormAttachment(iconLabel, 5);
		formData.right = new FormAttachment(100, -15);
		formData.bottom = new FormAttachment(100, -5);
		progressBar.setLayoutData(formData);
	}

	/**
	 * Updates this widget with the latest progress information:
	 * <ul>
	 * <li>Updates the timing information</li>
	 * <li>Updates the progressbar</li>
	 * <li>Update the state label and icon if required</li>
	 * </ul>
	 */
	@Override
	public void refresh() {
		super.refresh();
		if (!(getData() instanceof TaskInfo)) {
			return;
		}
		TaskInfo info = (TaskInfo) getData();
		State state = info.getSate();
		if (state != lastState) {
			lastState = state;
			iconLabel.setImage(getImage(state));
		}
		if ((state == State.ABORTED) || (state == State.FAILED)) {
			// progressBar.setEnabled(false);
		}
		progressBar.setSelection(((Double) (info.getProgress() * 100))
				.intValue());
		timeLabel.setText(getTimeText(info));
	}

	@Override
	public void setData(Object data) {
		super.setData(data);
		taskInfo = (TaskInfo) data;
		nameLabel.setText(taskInfo.getName());
		numLabel.setText(taskInfo.getTaskPosition() + 1 + "/" //$NON-NLS-1$
				+ taskInfo.getParentJob().getTaskNumber());
	}

}
