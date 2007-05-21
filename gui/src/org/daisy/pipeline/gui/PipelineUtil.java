package org.daisy.pipeline.gui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
    public static final String DOC_DIR_PATH = "/doc";
    public static final String SCRIPT_DIR_PATH = "/scripts";
    public static final String SCRIPT_DOC_DIR_PATH = DOC_DIR_PATH + "/scripts";
    public static final String TRANS_DIR_PATH = "/transformers";
    public static final String TRANS_DOC_DIR_PATH = DOC_DIR_PATH
            + "/transformers";
    public static final String USER_DOC_DIR_PATH = DOC_DIR_PATH + "/enduser";

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

    public static EFolder getScriptDir() {
        return getDir(SCRIPT_DIR_PATH);
    }

    private static EFolder fetchDir(String path) {
        EFolder dir = null;
        Bundle coreBundle = Platform.getBundle(GuiPlugin.CORE_ID);
        try {
            URL url = FileLocator.toFileURL(coreBundle.getEntry(path));
            dir = new EFolder(url.getPath());
        } catch (Exception e) {
            GuiPlugin.get().error("Couldn't find the documentation directory",
                    e);
        }
        return dir;
    }
}
