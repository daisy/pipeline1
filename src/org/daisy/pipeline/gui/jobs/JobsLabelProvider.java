package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
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

    public Image getColumnImage(Object element, int columnIndex) {
        if (element instanceof JobInfo && columnIndex == 0) {
            switch (((JobInfo) element).getSate()) {
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
