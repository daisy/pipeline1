package org.daisy.pipeline.scripts.ui;

import java.io.File;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * NLS Support.
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.scripts.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Returns the localized name of the given file (it should be a directory
	 * containing scripts). The returned value is the message associated with
	 * the key corresponding to the file's name.
	 * 
	 * @param file
	 *            a file (it should be a directory containing scripts)
	 * @return the localized name of the file
	 */
	public static String getName(File file) {
		try {
			return RESOURCE_BUNDLE.getString(file.getName());
		} catch (MissingResourceException e) {
			return file.getName();
		}
	}

	private Messages() {
	}
}
