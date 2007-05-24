package org.daisy.pipeline.gui.util;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 *
 */
public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.util.messages"; //$NON-NLS-1$
    public static String action_browseBack;
    public static String action_browseBack_tooltip;
    public static String action_collapseAll;
    public static String action_expandAll;
    public static String action_forward;
    public static String action_forward_tooltip;
    public static String button_browse;
    public static String button_deselectAll;
    public static String button_selectAll;
    public static String dialog_operationError;
    public static String groupBy_categorySet_none;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
