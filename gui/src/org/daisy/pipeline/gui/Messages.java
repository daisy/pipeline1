package org.daisy.pipeline.gui;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 * 
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.messages"; //$NON-NLS-1$
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String menu_edit;
    public static String menu_file;
    public static String menu_help;
    public static String menu_new;
    public static String menu_window;
    public static String menu_window_navigation;
    public static String menu_window_openPerspective;
    public static String menu_window_showView;
    public static String window_title;
}
