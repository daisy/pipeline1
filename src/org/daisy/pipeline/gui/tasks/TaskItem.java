package org.daisy.pipeline.gui.tasks;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.util.Timer;
import org.daisy.pipeline.gui.util.swt.CompositeItem;
import org.daisy.pipeline.gui.util.swt.CompositeList;
import org.daisy.util.execution.State;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Romain Deltour
 * 
 */
public class TaskItem extends CompositeItem {

    private Label iconLabel;

    private Label nameLabel;

    private Label timeLabel;

    private ProgressBar progressBar;

    private ToolBar toolBar;

    private State lastState;

    public TaskItem(CompositeList<TaskItem> parent, int style, int index) {
        super(parent, style, index);
        createChildren();
        setLayoutsForNoProgress();
        setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    }

    @Override
    public void refresh() {
        super.refresh();
        if (!(getData() instanceof TaskInfo)) {
            return;
        }
        TaskInfo info = (TaskInfo) getData();
        nameLabel.setText(info.getName());
        progressBar.setSelection(((Double) (info.getProgress() * 100))
                .intValue());
        timeLabel.setText(getTimeText(info));
        State state = info.getSate();
        if (state != lastState) {
            lastState = state;
            iconLabel.setImage(getImage(state));
        }
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

    /**
     * @return
     */
    private Image getImage(State state) {
        Image image = null;
        switch (state) {
        case IDLE:
            image = GuiPlugin.getImage(IIconsKeys.STATE_IDLE);
            break;
        case RUNNING:
            image = GuiPlugin.getImage(IIconsKeys.STATE_RUNNING);
            break;
        case FINISHED:
            image = GuiPlugin.getImage(IIconsKeys.STATE_FINISHED);
            break;
        default:
            break;
        }
        return image;
    }

    private void createChildren() {
        FormData formData;
        FormLayout layout = new FormLayout();
        setLayout(layout);

        // Create the icon label on the left
        iconLabel = new Label(this, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(0, 5);
        iconLabel.setLayoutData(formData);

        // Create action bar on the right
        toolBar = new ToolBar(this, SWT.FLAT);
        // TODO check if toolBar.setCursor() is necessary
        toolBar.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
        formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        toolBar.setLayoutData(formData);

        // Create name label
        nameLabel = new Label(this, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(0, 5);
        formData.left = new FormAttachment(iconLabel, 5);
        formData.right = new FormAttachment(toolBar, -5);
        nameLabel.setLayoutData(formData);

        // Create doc button
        // docButton = new ToolItem(toolBar, SWT.PUSH);
        // docButton.setToolTipText("Show documentation");
        // docButton.setImage(GuiPlugin.get().getImageRegistry()
        // .get(IK_DOC_BUTTON));
        // docButton.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // showDoc();
        // }
        // });

        // Create time info label
        timeLabel = new Label(this, SWT.NONE);
        formData = new FormData();
        formData.top = new FormAttachment(nameLabel, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        timeLabel.setLayoutData(formData);

        // Create Progress Bar
        progressBar = new ProgressBar(this, SWT.HORIZONTAL);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        formData = new FormData();
        formData.top = new FormAttachment(timeLabel, 5);
        formData.left = new FormAttachment(0, 5);
        formData.right = new FormAttachment(100, -5);
        progressBar.setLayoutData(formData);
    }

    private void setLayoutsForNoProgress() {

    }
}
