/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.progress;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.model.TaskInfo;
import org.daisy.pipeline.gui.util.Timer;
import org.daisy.pipeline.gui.util.swt.CompositeItem;
import org.daisy.pipeline.gui.util.swt.CompositeList;
import org.daisy.util.execution.State;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * @author Romain Deltour
 * 
 */
public class TaskItem extends CompositeItem {

    private Label iconLabel;

    private Label nameLabel;

    private Label numLabel;

    private Label timeLabel;

    private ProgressBar progressBar;

    private State lastState;

    private Font smallerFont;

    public TaskItem(CompositeList<TaskItem> parent, int style, int index) {
        super(parent, style, index);
    }

    @Override
    public void dispose() {
        if (smallerFont != null) {
            smallerFont.dispose();
        }
        super.dispose();
    }

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
        if (state == State.ABORTED || state == State.FAILED) {
            // progressBar.setEnabled(false);
        }
        nameLabel.setText(info.getName());
        progressBar.setSelection(((Double) (info.getProgress() * 100))
                .intValue());
        timeLabel.setText(getTimeText(info));
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
        timeLabel.setFont(getSmallerFont(timeLabel.getFont()));

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

    private Font getSmallerFont(Font font) {
        if (smallerFont == null) {
            FontData[] fd = font.getFontData();
            for (int i = 0; i < fd.length; i++) {
                fd[i].setHeight(fd[i].height - 1);
            }
            smallerFont = new Font(getDisplay(), fd);
        }
        return smallerFont;
    }

    /**
     * @return
     */
    private Image getImage(State state) {
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

    private String getTimeText(TaskInfo info) {
        String text;
        switch (info.getSate()) {
        case RUNNING:
            text = Timer.format(info.getTimer().getLeftTime()) + " ms left";
            break;
        case FINISHED:
            text = "Done in " + Timer.format(info.getTimer().getTotalTime())
                    + " ms";
            break;
        default:
            text = "";
            break;
        }
        return text;
    }

}
