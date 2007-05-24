package org.daisy.pipeline.gui.doc;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.doc.messages"; //$NON-NLS-1$
    public static String action_synchronize;
    public static String action_synchronize_tooltip;
    public static String error_noBrowser;
    public static String tab_help;
    public static String tab_help_tooltip;
    public static String tab_script;
    public static String tab_script_tooltip;
    public static String tab_transformers;
    public static String tab_transformers_tooltip;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
