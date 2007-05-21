package org.daisy.pipeline.gui.util;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
public class TableViewLabelProvider extends LabelProvider implements
        ITableLabelProvider, IFontProvider {
    ITableField[] fields;

    /**
     * Create a neew instance of the receiver.
     * 
     * @param fields
     */
    public TableViewLabelProvider(ITableField[] fields) {
        this.fields = fields;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
     *      int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        if (fields == null || columnIndex < 0 || columnIndex >= fields.length) {
            return null;
        }
        return fields[columnIndex].getImage(element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
     *      int)
     */
    public String getColumnText(Object element, int columnIndex) {
        if (fields == null || columnIndex < 0 || columnIndex >= fields.length) {
            return null;
        }
        return fields[columnIndex].getText(element);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
     */
    public Font getFont(Object element) {
        if (element instanceof Category) {
            return JFaceResources.getFontRegistry().getBold(
                    JFaceResources.DEFAULT_FONT);
        }
        return null;
    }
    
    

}
