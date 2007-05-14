package org.daisy.pipeline.gui.util;

import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
public interface ITableField {

    public String getHeaderText();

    public Image getHeaderImage();

    public Image getImage(Object element);

    public String getText(Object element);

    public int getWeight();
}
