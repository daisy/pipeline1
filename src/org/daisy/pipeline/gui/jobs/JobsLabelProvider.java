package org.daisy.pipeline.gui.jobs;

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
        GuiPlugin.get().getImageRegistry().put(IK_IDLE,
                GuiPlugin.getImageDescriptor("icons/progress_task.gif"));
        GuiPlugin.get().getImageRegistry().put(IK_RUNNING,
                GuiPlugin.getImageDescriptor("icons/progress-indicator.gif"));
        GuiPlugin.get().getImageRegistry().put(IK_FINISHED,
                GuiPlugin.getImageDescriptor("icons/tick.png"));
    }

    public Image getColumnImage(Object element, int columnIndex) {
        if (element instanceof JobInfo && columnIndex == 0) {
            switch (((JobInfo) element).getSate()) {
            // case ABORTED:
            // return GuiPlugin.get().getImageRegistry().get(IK_);
            // case FAILED:
            // return GuiPlugin.get().getImageRegistry().get(IK_IDLE);
            case FINISHED:
                return GuiPlugin.get().getImageRegistry().get(IK_FINISHED);
            case IDLE:
                return GuiPlugin.get().getImageRegistry().get(IK_IDLE);
            case RUNNING:
                return GuiPlugin.get().getImageRegistry().get(IK_RUNNING);
                // case WAITING:
                // return GuiPlugin.get().getImageRegistry().get(IK_IDLE);
            }
            return GuiPlugin.get().getImageRegistry().get(IK_IDLE);
        }
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        // Note: labels for parameters are custom-painted in JobsView class
        if (element instanceof JobInfo) {
            JobInfo job = (JobInfo) element;
            switch (columnIndex) {
            case 0:
                return job.getName();
            case 1:
                return job.getSate().toString();
            }
        }
        return null;
    }
}
