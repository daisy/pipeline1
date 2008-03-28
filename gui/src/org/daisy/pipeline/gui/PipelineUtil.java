/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.gui;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.daisy.util.file.EFolder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.Preferences;

/**
 * A set of utility methods and constants to handle the Pipeline core paths and
 * properties.
 * 
 * @author Romain Deltour
 * 
 */
public final class PipelineUtil {

	/** A cache of fetched Pipeline core directories */
	private static final Map<String, EFolder> dirMap = new HashMap<String, EFolder>();
	// Directory Paths
	/** The path to the documentation directory */
	public static final String DOC_DIR = "/doc"; //$NON-NLS-1$
	/** The path to the home directory */
	public static final String HOME_DIR = "/"; //$NON-NLS-1$
	/** The path to the scripts directory */
	public static final String SCRIPT_DIR = "/scripts"; //$NON-NLS-1$
	/** The path to the scripts documentation directory */
	public static final String SCRIPT_DOC_DIR = DOC_DIR + "/scripts"; //$NON-NLS-1$
	/** The path to the transformers directory */
	public static final String TRANS_DIR = "/transformers"; //$NON-NLS-1$
	/** The path to the transformers documentation directory */
	public static final String TRANS_DOC_DIR = DOC_DIR + "/transformers"; //$NON-NLS-1$
	/** The path to the user documentation directory */
	public static final String USER_DOC_DIR = DOC_DIR + "/enduser"; //$NON-NLS-1$
	// Property Keys
	/** The name of the System properties used for the path to ImageMagick */
	public static final String PATH_TO_IMAGEMAGICK_PROP = "pipeline.imageMagick.converter.path"; //$NON-NLS-1$
	/** The name of the System properties used for the path to Lame */
	public static final String PATH_TO_LAME_PROP = "dmfc.lame.path"; //$NON-NLS-1$
	/** The name of the System properties used for the path to Python */
	public static final String PATH_TO_PYTHON_PROP = "pipeline.python.path"; //$NON-NLS-1$
	/** The name of the System properties used for the path to SoX */
	public static final String PATH_TO_SOX_PROP = "pipeline.sox.path"; //$NON-NLS-1$
	/**
	 * The name of the System properties used for the path to the temporary
	 * directory
	 */
	public static final String PATH_TO_TEMP_DIR_PROP = "dmfc.tempDir"; //$NON-NLS-1$
	/** The URI to the page to show when a doc has not been found */
	public static final URI DOC_404 = new File(getDir(DOC_DIR), "404.html") //$NON-NLS-1$
			.toURI();

	/**
	 * Converts the {@link Preferences} used by the Pipeline GUI in
	 * {@link Properties} used by the Pipeline core.
	 * 
	 * @return The set of properties converted from the GUI-set preference.
	 */
	public static Properties convPrefToProperties() {
		Properties properties = new Properties();
		properties.setProperty(PATH_TO_IMAGEMAGICK_PROP, PreferencesUtil.get(
				PreferencesKeys.PATH_TO_IMAGEMAGICK,
				PreferencesKeys.PATH_TO_IMAGEMAGICK_DEFAULT));
		properties.setProperty(PATH_TO_LAME_PROP, PreferencesUtil.get(
				PreferencesKeys.PATH_TO_LAME,
				PreferencesKeys.PATH_TO_LAME_DEFAULT));
		properties.setProperty(PATH_TO_SOX_PROP, PreferencesUtil.get(
				PreferencesKeys.PATH_TO_SOX,
				PreferencesKeys.PATH_TO_SOX_DEFAULT));
		properties.setProperty(PATH_TO_TEMP_DIR_PROP, PreferencesUtil.get(
				PreferencesKeys.PATH_TO_TEMP_DIR,
				PreferencesKeys.PATH_TO_TEMP_DIR_DEFAULT));
		return properties;
	}

	/**
	 * Fetch the folder corresponding to the given path from the bundle context
	 * using the {@link FileLocator} API.
	 * 
	 * @param path
	 *            A path rooting in the Pipeline core bundle.
	 * @return the folder object corresponding to <code>path</code>.
	 */
	private static EFolder fetchDir(String path) {
		EFolder dir = null;
		Bundle coreBundle = Platform.getBundle(GuiPlugin.CORE_ID);
		try {
			URL url = FileLocator.toFileURL(coreBundle.getEntry(path));
			dir = new EFolder(url.getPath());
		} catch (Exception e) {
			GuiPlugin.get().error("Couldn't find the " + path + " directory", //$NON-NLS-1$ //$NON-NLS-2$
					e);
		}
		return dir;
	}

	/**
	 * Returns the folder object corresponding to the given path description.
	 * 
	 * @param path
	 *            A path rooting in the Pipeline core bundle.
	 * @return the folder object corresponding to <code>path</code>.
	 */
	public static EFolder getDir(String path) {
		EFolder dir = dirMap.get(path);
		if (dir == null) {
			dir = fetchDir(path);
			dirMap.put(path, dir);
		}
		return dir;
	}

	// Prevents from instantiating this static utility
	private PipelineUtil() {
	}
}
