package org.daisy.pipeline.gui;

/**
 * Lists the preferences keys used in this plugin.
 * 
 * @author Romain Deltour
 * 
 */
public interface PreferencesKeys {

	/** The preference holding the last file path used as input parameter. */
	public static final String LAST_SELECTED_INPUT = "LAST_SELECTED_INPUT"; //$NON-NLS-1$
	/** The preference holding the last file path used as output parameter. */
	public static final String LAST_SELECTED_OUTPUT = "LAST_SELECTED_OUTPUT"; //$NON-NLS-1$

	/** The preference holding the path to ImageMagick */
	public static final String PATH_TO_IMAGEMAGICK = "PATH_TO_IMAGEMAGICK"; //$NON-NLS-1$
	/** The preference holding the default path to ImageMagick */
	public static final String PATH_TO_IMAGEMAGICK_DEFAULT = "/path/to/convert.exe"; //$NON-NLS-1$
	/** The preference holding the path to lame */
	public static final String PATH_TO_LAME = "PATH_TO_LAME"; //$NON-NLS-1$
	/** The preference holding the default path to lame */
	public static final String PATH_TO_LAME_DEFAULT = "/path/to/lame.exe"; //$NON-NLS-1$
	/** The preference holding the path to python */
	public static final String PATH_TO_PYTHON = "PATH_TO_PYTHON"; //$NON-NLS-1$
	/** The preference holding the default path to python */
	public static final String PATH_TO_PYTHON_DEFAULT = "/path/to/python.exe"; //$NON-NLS-1$
	/** The preference holding the path to the temporary directory */
	public static final String PATH_TO_TEMP_DIR = "PATH_TO_TEMP_DIR"; //$NON-NLS-1$	
	/** The preference holding the default path to the temporary directory */
	public static final String PATH_TO_TEMP_DIR_DEFAULT = "/path/to/tmp"; //$NON-NLS-1$

	/** The preference holding the path of the last selected zip patch. */
	public static final String UPDATE_LAST_SELECTED_ZIP = "UPDATE_LAST_SELECTED_ZIP"; //$NON-NLS-1$
}
