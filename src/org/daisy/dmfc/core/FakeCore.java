package org.daisy.dmfc.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;

import org.daisy.dmfc.core.script.Script;
import org.daisy.dmfc.core.script.ScriptValidationException;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * @author Romain Deltour
 * 
 */
public class FakeCore {
    Bundle coreBundle;

    public FakeCore(InputListener inListener) throws DMFCConfigurationException {
        coreBundle = Platform.getBundle(PipelineGuiPlugin.CORE_ID);
        loadProperties();

    }

    public Script newScript(URL url) throws ScriptValidationException {
        Script script = null;
        try {
            Class parserClass = coreBundle
                    .loadClass("org.daisy.dmfc.core.script.Parser");
            Method getInstance = parserClass.getMethod("getInstance",
                    new Class[0]);
            getInstance.setAccessible(true);
            Object parser = getInstance.invoke(null, new Object[0]);
            Method newScript = parserClass.getMethod("newScript",
                    new Class[] { URL.class });
            newScript.setAccessible(true);
            script = (Script) newScript.invoke(parser, new Object[] { url });
        } catch (Exception e) {
            throw new ScriptValidationException(e.getCause().getMessage(), e
                    .getCause());
        }
        // TODO set fake transformer handlers
        // this.setTransformerHandlers(script);
        return script;
    }

    private void loadProperties() {
        URL url = coreBundle.getEntry("/src/dmfc.properties");
        Properties props = new Properties(System.getProperties());
        try {
            props.load(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.setProperties(props);
    }

}
