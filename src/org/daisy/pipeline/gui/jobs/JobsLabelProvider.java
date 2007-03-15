package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.jobs.model.Job;
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
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        String result;
        Job job = (Job) element;
        switch (columnIndex) {
        case 0: // COMPLETED_COLUMN - checked or not
            result = job.getStatus().getLocalizedString();
            break;
        case 1:
            result = job.getScript().getName();
            break;
        case 2:
            result = job.getInputFile().getName();
            break;
        case 3:
            result = (job.getOutputFile() != null)?job.getOutputFile().getPath():"";
            break;
        default:
            // this shouldn't happen
            result="err!";
            break;
        }
        return result;
    }
}