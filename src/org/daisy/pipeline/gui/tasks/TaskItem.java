package org.daisy.pipeline.gui.tasks;

import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.daisy.pipeline.gui.util.StateObject;
import org.daisy.pipeline.gui.util.swt.CompositeItem;
import org.daisy.pipeline.gui.util.swt.CompositeList;
import org.daisy.util.execution.State;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Romain Deltour
 * 
 */
public class TaskItem extends CompositeItem {
    private static String IK_DOC_BUTTON = "org.daisy.pipeline.gui.jobdetails.DOC_BUTTON";
    private static String IK_IDLE = "org.daisy.pipeline.gui.jobdetails.IDLE";
    private static String IK_RUNNING = "org.daisy.pipeline.gui.jobdetails.RUNNING";
    private static String IK_FINISHED = "org.daisy.pipeline.gui.jobdetails.FINISHED";
    static {
        PipelineGuiPlugin.getDefault().getImageRegistry().put(IK_DOC_BUTTON,
                PipelineGuiPlugin.getIcon(IIconsKeys.HELP_BROWSER));
        PipelineGuiPlugin.getDefault().getImageRegistry()
                .put(
                        IK_IDLE,
                        PipelineGuiPlugin
                                .getImageDescriptor("icons/progress_task.gif"));
        PipelineGuiPlugin.getDefault().getImageRegistry().put(
                IK_RUNNING,
                PipelineGuiPlugin
                        .getImageDescriptor("icons/progress-indicator.gif"));
        PipelineGuiPlugin.getDefault().getImageRegistry().put(IK_FINISHED,
                PipelineGuiPlugin.getImageDescriptor("icons/tick.png"));
    }
    private Label iconLabel;
    private Label nameLabel;
    private Label timeLabel;
    private ProgressBar progressBar;
    private ToolBar toolBar;
    private ToolItem docButton;
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
        nameLabel.setText(info.getNiceName());
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
            text = StateObject.format(info.getLeftTime()) + " ms left";
            break;
        case FINISHED:
            text = "Done in " + StateObject.format(info.getTotalTime())
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
            image = PipelineGuiPlugin.getDefault().getImageRegistry().get(
                    IK_IDLE);
            break;
        case RUNNING:
            image = PipelineGuiPlugin.getDefault().getImageRegistry().get(
                    IK_RUNNING);
            break;
        case FINISHED:
            image = PipelineGuiPlugin.getDefault().getImageRegistry().get(
                    IK_FINISHED);
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
        docButton = new ToolItem(toolBar, SWT.PUSH);
        docButton.setToolTipText("Show documentation");
        docButton.setImage(PipelineGuiPlugin.getDefault().getImageRegistry()
                .get(IK_DOC_BUTTON));
        docButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showDoc();
            }
        });

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

    private void showDoc() {
        // TODO implem showDoc()
        System.out.println("show doc");
    }
}
