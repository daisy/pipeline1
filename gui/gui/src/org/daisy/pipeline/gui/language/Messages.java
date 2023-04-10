package org.daisy.pipeline.gui.language;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.language.messages"; //$NON-NLS-1$

	/** The title of the error dialog if an exception occurred */
	public static String langswitch_error_title;
	/** The message of the error dialog if an exception occurred */
	public static String langswitch_error_message;
	/** The title of the dialog asking the user to restart */
	public static String langswitch_restart_title;
	/** The message of the dialog asking the user to restart */
	public static String langswitch_restart_message;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
