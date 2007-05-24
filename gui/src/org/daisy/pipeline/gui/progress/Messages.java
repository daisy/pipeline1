package org.daisy.pipeline.gui.progress;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.progress.messages"; //$NON-NLS-1$
    public static String button_cancel_tooltip;
    public static String label_noJob;
    public static String label_state;
    public static String uiJob_progressUpdate;
    public static String uiJob_taskUpdate_name;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
