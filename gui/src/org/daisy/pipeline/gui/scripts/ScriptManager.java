package org.daisy.pipeline.gui.scripts;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.event.CoreMessageEvent;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.dmfc.core.script.Script;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.util.file.EFolder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class ScriptManager {
    private static ScriptManager _default = new ScriptManager();

    private EFolder scriptDir;

    private Map<URI, Script> scriptMap;

    public ScriptManager() {
        Bundle coreBundle = Platform.getBundle(GuiPlugin.CORE_ID);
        try {
            URL url = FileLocator.toFileURL(coreBundle.getEntry("/scripts"));

            scriptDir = new EFolder(url.getPath());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        scriptMap = new HashMap<URI, Script>();
        populateScripts();
    }

    public static ScriptManager getDefault() {
        return _default;
    }

    public void init() {
        populateScripts();
    }

    public void dispose() {
        // Nothing
    }

    public EFolder getScriptDir() {
        return scriptDir;
    }

    public Script getScript(URI uri) {
        return scriptMap.get(uri);
    }

    private void populateScripts() {
        Collection scripts = scriptDir.getFiles(true, ".+\\.taskScript");
        DMFCCore core = GuiPlugin.get().getCore();
        for (Iterator iter = scripts.iterator(); iter.hasNext();) {
            File file = (File) iter.next();
            Script script = null;
            try {
                script = core.newScript(file.toURI().toURL());
                scriptMap.put(file.toURI(), script);
            } catch (Exception e) {
                EventBus.getInstance().publish(
                        new CoreMessageEvent(this, "Unable to load script "
                                + file.getName(), MessageEvent.Type.WARNING));
                GuiPlugin.get().error(e.getLocalizedMessage(), e);
            }
        }
    }
}
