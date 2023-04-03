package org.daisy.pipeline.gui.update;

import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.update.messages"; //$NON-NLS-1$
	/** The title of the update wizard */
	public static String wizard_title;
	/** The text to show when the ZIP patch description is not available */
	public static String metadata_description_NA;
	/** The text to show when the ZIP patch version is not available */
	public static String metadata_version_NA;
	/** The title of the ZIP update wizard page */
	public static String zipPage_title;
	/** The info message of the ZIP update wizard page */
	public static String zipPage_message_info;
	/** The label of the ZIP path field */
	public static String zipPage_path_label;
	/** The label of the description area for the ZIP patch */
	public static String zipPage_descr_label;
	/** The label of the tree content area for the ZIP patch */
	public static String zipPage_content_label;
	/** The title of the warning dialog shown when the version is not compatible */
	public static String zipPage_dialog_warning_version_title;
	/** The message of the warning dialog for incompatible version */
	public static String zipPage_dialog_warning_version_question;
	/** The title of the update error dialog */
	public static String zipPage_dialog_error_title;
	/** The message of the update error dialog */
	public static String zipPage_dialog_error_message;
	/** The title of the success dialog */
	public static String zipPage_dialog_ok_title;
	/** The question of the success dialog asking for restart */
	public static String zipPage_dialog_ok_confirm;
	/** The error message when the zip patch is invalid */
	public static String zipPage_message_error_invalidZip;
	/** The error message when the zip patch could not be read */
	public static String zipPage_message_error_readAccess;
	/** The warning message when the metadata is not available */
	public static String zipPage_message_metadataNA;
	/** The progress message when a file is being updated */
	public static String zipOperation_monitor_updatingFile;
	/** The progress message when a directory is being updated */
	public static String zipOperation_monitor_updatingDir;
	/** The main progress message */
	public static String zipOperation_monitor_mainTask;
	/** The message for an error that occurred while creating a directory */
	public static String zipOperation_error_directory;
	/** The message for an error that occurred while updating a file */
	public static String zipOperation_error_file;
	/** The warning message when the patch version is not compatible */
	public static String zipPage_message_warning_version;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
