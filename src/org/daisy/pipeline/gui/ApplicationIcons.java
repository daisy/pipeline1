package org.daisy.pipeline.gui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public final class ApplicationIcons {
    public static final String ICON_DIR = "icons/tango/";
    public static final String SIZE_16 = ICON_DIR + "16x16/";
    public static final String CAT_ACTION = "actions/";
    public static final String UNDO = SIZE_16 + CAT_ACTION + "edit-undo.png";
    public static final String REDO = SIZE_16 + CAT_ACTION + "edit-redo.png";

    public static ImageDescriptor getImageDescriptor(String key) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(Application.PLUGIN_ID, key);
    }

    public ApplicationIcons() {
    }
    
    
}
