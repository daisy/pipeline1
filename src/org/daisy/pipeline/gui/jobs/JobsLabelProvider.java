package org.daisy.pipeline.gui.jobs;

import org.daisy.dmfc.core.script.JobParameter;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * @author Laurie Sherve
 */
public class JobsLabelProvider extends LabelProvider implements
        ITableLabelProvider {
    private static String IK_IDLE = "org.daisy.pipeline.gui.jobs.IDLE";
    private static String IK_RUNNING = "org.daisy.pipeline.gui.jobs.RUNNING";
    private static String IK_FINISHED = "org.daisy.pipeline.gui.jobs.FINISHED";
    static {
        GuiPlugin.get().getImageRegistry()
                .put(
                        IK_IDLE,
                        GuiPlugin
                                .getImageDescriptor("icons/progress_task.gif"));
        GuiPlugin.get().getImageRegistry().put(
                IK_RUNNING,
                GuiPlugin
                        .getImageDescriptor("icons/progress-indicator.gif"));
        GuiPlugin.get().getImageRegistry().put(IK_FINISHED,
                GuiPlugin.getImageDescriptor("icons/tick.png"));
    }

    public Image getColumnImage(Object element, int columnIndex) {
        if (element instanceof JobInfo && columnIndex == 0) {
            return GuiPlugin.get().getImageRegistry().get(
                    IK_IDLE);
        }
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof JobInfo) {
            return getText((JobInfo) element, columnIndex);
        }
        if (element instanceof JobParameter) {
            return getText((JobParameter) element, columnIndex);
        }
        return "err!";
    }

    private String getText(JobInfo job, int columnIndex) {
        String text;
        switch (columnIndex) {
        case 0:
            text = job.getJob().getScript().getNicename();
            break;
        default:
            text = "";
            break;
        }
        return text;
    }

    private String getText(JobParameter param, int columnIndex) {
        String text;
        switch (columnIndex) {
        case 0:
            text = param.getScriptParameter().getNicename();
            break;
        case 1:
            text = param.getValue();
            break;
        default:
            text = "";
            break;
        }
        return text;
    }
}
