package org.daisy.pipeline.gui.jobs.wizard;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.jobs.wizard.messages"; //$NON-NLS-1$
    public static String helpTray_close;
    public static String helpTray_close_tooltip;
    public static String page_param_description;
    public static String page_param_error_invalid;
    public static String page_param_optionalGroup;
    public static String page_param_requiredGroup;
    public static String page_param_title;
    public static String page_script_description;
    public static String page_script_error_unhandledScript;
    public static String page_script_title;
    public static String wizard_title;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
