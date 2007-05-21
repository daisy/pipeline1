package org.daisy.pipeline.gui.doc;

import java.io.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
public class DocFileLabelProvider extends LabelProvider {
    private TocImageProvider imageProvider;

    public DocFileLabelProvider(TocImageProvider imageProvider) {
        super();
        this.imageProvider = imageProvider;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof File) {
            File file = (File) element;
            if (file.isFile()) {
                // TODO fetch the html title
            }
            return file.getName();
        }
        return null;
    }

    @Override
    public Image getImage(Object element) {
        return imageProvider.getImage(element);
    }

}
