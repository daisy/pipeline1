package org.daisy.pipeline.gui.parameters;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.parameters.messages"; //$NON-NLS-1$
    public static String heading_name;
    public static String heading_value;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
