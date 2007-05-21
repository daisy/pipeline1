package org.daisy.pipeline.gui.scripts;

import java.io.File;
import java.net.URI;
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
import org.daisy.pipeline.gui.PipelineUtil;
import org.daisy.util.file.EFolder;

public class ScriptManager {
    private static ScriptManager _default = new ScriptManager();

    private Map<URI, Script> scriptMap;

    public ScriptManager() {
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

    public Script getScript(URI uri) {
        return scriptMap.get(uri);
    }

    public Collection<Script> getScripts() {
        return scriptMap.values();
    }

    public URI getURI(Script script) {
        if (script != null) {
            for (URI uri : scriptMap.keySet()) {
                if (script.equals(scriptMap.get(uri))) {
                    return uri;
                }
            }
        }
        return null;
    }

    private void populateScripts() {
        EFolder scriptDir = PipelineUtil.getScriptDir();
        // TODO set a better script filter with peeker
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
