package org.daisy.pipeline.gui.parameters;

import org.daisy.dmfc.core.script.JobParameter;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
public class ParamsLabelProvider extends LabelProvider implements
        ITableLabelProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
     *      int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
     *      int)
     */
    public String getColumnText(Object element, int columnIndex) {
        JobParameter param = (JobParameter) element;
        String text;
        switch (columnIndex) {
        case 0:
            text = param.getScriptParameter().getNicename();
            break;
        default:
            text = param.getValue();
            break;
        }
        return text;
    }

}