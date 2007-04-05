package org.daisy.pipeline.gui.scripts;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.daisy.dmfc.core.FakeCore;
import org.daisy.dmfc.core.script.Script;
import org.daisy.dmfc.core.script.ScriptValidationException;
import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.daisy.util.file.EFolder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class ScriptManager {
    private static ScriptManager instance;
    private EFolder scriptDir;
    private Map<String, Script> scriptMap;

    private ScriptManager() {
        Bundle coreBundle = Platform.getBundle(PipelineGuiPlugin.CORE_ID);
        try {
            URL url = FileLocator.toFileURL(coreBundle.getEntry("/scripts"));
            scriptDir = new EFolder(url.toURI());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        scriptMap = new HashMap<String, Script>();
        populateScriptMap();
    }

    public static ScriptManager getDefault() {
        if (instance == null) {
            instance = new ScriptManager();
        }
        return instance;
    }

    public EFolder getScriptDir() {
        return scriptDir;
    }

    public Script getScript(String path) {
        return scriptMap.get(path);
    }

    private void populateScriptMap() {
        // TODO implem lazy loading of scripts instead
        Collection scripts = scriptDir.getFiles(true, ".+\\.taskScript");
        for (Iterator iter = scripts.iterator(); iter.hasNext();) {
            File file = (File) iter.next();
            Script script = null;
            // TODO fake code
            FakeCore core = PipelineGuiPlugin.getDefault().getCore();
            try {
                script = core.newScript(file.toURL());
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ScriptValidationException e) {
                // TODO Auto-generated catch block
                System.err.println("unable to create script at "
                        + file.toString());
                System.err.println("caused by: " + e.getMessage());
                // e.printStackTrace();
            }
            if (script != null) {
                scriptMap.put(file.getPath(), script);
            }
        }
    }
}
