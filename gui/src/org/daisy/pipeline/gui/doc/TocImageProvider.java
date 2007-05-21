package org.daisy.pipeline.gui.doc;

import java.io.File;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.util.file.EFolder;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
public class TocImageProvider {

    EFolder root;

    public TocImageProvider(EFolder root) {
        super();
        this.root = root;
    }

    public Image getImage(Object element) {
        if (element instanceof File) {
            File file = (File) element;
            if (file.isDirectory()) {
                if (root != null && root.equals(file.getParentFile())) {
                    return GuiPlugin.getImage(IIconsKeys.HELP_TOC_SECTION);
                } else {
                    return GuiPlugin.getImage(IIconsKeys.HELP_TOC_SUBSECTION);
                }
            } else {
                return GuiPlugin.getImage(IIconsKeys.HELP_TOC_ITEM);
            }
        }
        return null;
    }
}
