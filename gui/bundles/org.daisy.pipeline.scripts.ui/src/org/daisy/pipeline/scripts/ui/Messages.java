package org.daisy.pipeline.scripts.ui;

import java.io.File;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "org.daisy.pipeline.scripts.ui.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
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

}
