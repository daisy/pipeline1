package org.daisy.pipeline.gui.doc;

import org.daisy.pipeline.gui.scripts.ScriptsLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
public class DocScriptLabelProvider extends ScriptsLabelProvider {

    private TocImageProvider imageProvider;

    public DocScriptLabelProvider(TocImageProvider imageProvider) {
        super();
        this.imageProvider = imageProvider;
    }

    @Override
    public Image getImage(Object element) {
        return imageProvider.getImage(element);
    }

}
