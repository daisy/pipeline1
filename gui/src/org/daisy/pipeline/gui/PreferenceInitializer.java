package org.daisy.pipeline.gui;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;

/**
 * Initializes the default values of the preferences used in the Pipeline GUI.
 * <p>
 * This class is referenced in the plugin.xml and is used to enable dynamic
 * initialization for the various OS-dependent paths to third-party executables
 * used in the Pipeline.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/** The default preferences scope */
	private IScopeContext defaultScope = new DefaultScope();

	/** Calls the super constructor */
	public PreferenceInitializer() {
		super();
	}

	/**
	 * Process the initialization steps common to all OSes.
	 */
	private void initCommonDefaults() {
		// set the default temp dir
		try {
			PreferencesUtil.put(PipelineUtil.PATH_TO_TEMP_DIR, File
					.createTempFile("dont", "care").getParent(), //$NON-NLS-1$ //$NON-NLS-2$
					defaultScope);
		} catch (IOException e) {
			GuiPlugin.get().warn("Couldn't find the default temp directory", e); //$NON-NLS-1$
		}
	}

	/**
	 * Called by the preference initializer to initialize default preference
	 * values.
	 * <p>
	 * This method first process the common initialization then forwards to
	 * OS-specific initialization methods.
	 * </p>
	 */
	@Override
	public void initializeDefaultPreferences() {
		initCommonDefaults();
		if (System.getProperty("os.name").startsWith("Windows")) { //$NON-NLS-1$ //$NON-NLS-2$
			initWindowsDefaults();
		} else if (System.getProperty("os.name").startsWith("Mac OS X")) { //$NON-NLS-1$ //$NON-NLS-2$
			initMacDefaults();
		} else if (System.getProperty("os.name").startsWith("Linux") //$NON-NLS-1$ //$NON-NLS-2$
				|| System.getProperty("os.name").startsWith("LINUX")) { //$NON-NLS-1$ //$NON-NLS-2$
			initLinuxDefaults();
		}

	}

	/**
	 * Process the initialization steps particular to Linux.
	 */
	private void initLinuxDefaults() {
		setPrefPath(PipelineUtil.PATH_TO_IMAGEMAGICK, "/usr/bin/imagemagick");//$NON-NLS-1$
		setPrefPath(PipelineUtil.PATH_TO_LAME, "/usr/bin/lame");//$NON-NLS-1$
		setPrefPath(PipelineUtil.PATH_TO_PYTHON, "/usr/bin/python");//$NON-NLS-1$
	}

	/**
	 * Process the initialization steps particular to Mac OS X.
	 */
	private void initMacDefaults() {
		setPrefPath(PipelineUtil.PATH_TO_IMAGEMAGICK, "/opt/local/bin/convert");//$NON-NLS-1$
		setPrefPath(PipelineUtil.PATH_TO_LAME, "/sw/bin/lame");//$NON-NLS-1$
		setPrefPath(PipelineUtil.PATH_TO_PYTHON, "/usr/bin/python");//$NON-NLS-1$
	}

	/**
	 * Process the initialization steps particular to MS Windows.
	 */
	private void initWindowsDefaults() {
		setPrefPath(PipelineUtil.PATH_TO_IMAGEMAGICK,
				"C:\\Program Files\\ImageMagick-6.3.5-Q16\\convert.exe");//$NON-NLS-1$
		setPrefPath(PipelineUtil.PATH_TO_LAME, "C:\\lame\\lame.exe");//$NON-NLS-1$
		setPrefPath(PipelineUtil.PATH_TO_PYTHON, "C:\\Python25\\python.exe");//$NON-NLS-1$
	}

	/**
	 * Sets the preference of the given name to the given path if the file
	 * represented by this path exists.
	 * 
	 * @param pref
	 *            The name of a preference
	 * @param path
	 *            A path of a file
	 */
	private void setPrefPath(String pref, String path) {
		File file = new File(path);
		if (file.exists()) {
			PreferencesUtil.put(pref, path, defaultScope);
		} else {
			PreferencesUtil.put(pref, "", defaultScope);
		}
	}
}
