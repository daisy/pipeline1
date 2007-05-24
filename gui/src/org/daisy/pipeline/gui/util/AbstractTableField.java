package org.daisy.pipeline.gui.util;

import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
public class AbstractTableField implements ITableField {

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.util.ITableField#getHeaderImage()
     */
    public Image getHeaderImage() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.util.ITableField#getHeaderText()
     */
    public String getHeaderText() {
        return ""; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.util.ITableField#getImage(java.lang.Object)
     */
    public Image getImage(Object element) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.util.ITableField#getText(java.lang.Object)
     */
    public String getText(Object element) {
        return ""; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.util.ITableField#getWeight()
     */
    public int getWeight() {
        // TODO Auto-generated method stub
        return 1;
    }

}
