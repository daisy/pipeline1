package org.daisy.pipeline.gui.model;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.event.CoreMessageEvent;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.dmfc.core.script.Script;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.PipelineUtil;
import org.daisy.pipeline.gui.scripts.ScriptFileFilter;
import org.eclipse.osgi.util.NLS;

public class ScriptManager {
    private static ScriptManager _default = new ScriptManager();

    private Map<URI, Script> scriptMap;

    public ScriptManager() {
        scriptMap = new HashMap<URI, Script>();
    }

    public static ScriptManager getDefault() {
        return _default;
    }

    public void init() {
        populateScripts(PipelineUtil.getScriptDir());
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

    private void populateScripts(File dir) {
        DMFCCore core = GuiPlugin.get().getCore();
        for (File file : dir.listFiles(new ScriptFileFilter(true))) {
            if (file.isDirectory()) {
                populateScripts(file);
            } else {
                Script script = null;
                try {
                    script = core.newScript(file.toURI().toURL());
                    scriptMap.put(file.toURI(), script);
                } catch (Exception e) {
                    EventBus.getInstance().publish(
                            new CoreMessageEvent(this, NLS.bind(
                                    Messages.error_unableToLoadScript, file
                                            .getName()),
                                    MessageEvent.Type.WARNING));
                    GuiPlugin.get().error(e.getLocalizedMessage(), e);
                }
            }
        }
    }
}
