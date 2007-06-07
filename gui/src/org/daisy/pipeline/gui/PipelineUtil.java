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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.daisy.util.file.EFolder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * @author Romain Deltour
 * 
 */
public final class PipelineUtil {

    private static final Map<String, EFolder> dirMap = new HashMap<String, EFolder>();
    // Directory Paths
    public static final String DOC_DIR = "/doc"; //$NON-NLS-1$
    public static final String HOME_DIR = "/"; //$NON-NLS-1$
    public static final String SCRIPT_DIR = "/scripts"; //$NON-NLS-1$
    public static final String SCRIPT_DOC_DIR = DOC_DIR + "/scripts"; //$NON-NLS-1$
    public static final String TRANS_DIR = "/transformers"; //$NON-NLS-1$
    public static final String TRANS_DOC_DIR = DOC_DIR + "/transformers"; //$NON-NLS-1$
    public static final String USER_DOC_DIR = DOC_DIR + "/enduser"; //$NON-NLS-1$
    // Preferences Keys
    public static final String PATH_TO_LAME = "PATH_TO_LAME"; //$NON-NLS-1$
    public static final String PATH_TO_LAME_DEFAULT = "/path/to/lame.exe"; //$NON-NLS-1$
    public static final String PATH_TO_PYTHON = "PATH_TO_PYTHON"; //$NON-NLS-1$
    public static final String PATH_TO_PYTHON_DEFAULT = "/path/to/python.exe"; //$NON-NLS-1$
    public static final String PATH_TO_TEMP_DIR = "PATH_TO_TEMP_DIR"; //$NON-NLS-1$
    public static final String PATH_TO_TEMP_DIR_DEFAULT = "/path/to/tmp"; //$NON-NLS-1$

    private PipelineUtil() {
    }

    public static EFolder getDir(String path) {
        EFolder dir = dirMap.get(path);
        if (dir == null) {
            dir = fetchDir(path);
            dirMap.put(path, dir);
        }
        return dir;
    }

    public static Properties convPrefToProperties() {
        Properties properties = new Properties();
        properties.setProperty("dmfc.lame.path", PreferencesUtil.get( //$NON-NLS-1$
                PATH_TO_LAME, PATH_TO_LAME_DEFAULT));
        properties.setProperty("pipeline.python.path", PreferencesUtil.get( //$NON-NLS-1$
                PATH_TO_PYTHON, PATH_TO_PYTHON_DEFAULT));
        properties.setProperty("dmfc.tempDir", PreferencesUtil.get( //$NON-NLS-1$
                PATH_TO_TEMP_DIR, PATH_TO_TEMP_DIR_DEFAULT));
        return properties;
    }

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
}
