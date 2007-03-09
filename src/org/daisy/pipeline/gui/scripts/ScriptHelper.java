package org.daisy.pipeline.gui.scripts;

import java.util.Map;

import org.daisy.dmfc.core.script.Property;
import org.daisy.dmfc.core.script.ScriptHandler;

public final class ScriptHelper {

    private final static String INPUT = "input";
    private final static String OUTPUT = "outputPath";

    private ScriptHelper() {
        // Nothing. This class is a static utility.
    }

    public static boolean isOutputRequired(ScriptHandler script) {
        return script.getProperties().get(OUTPUT) != null;
    }

    public static String getInputType(ScriptHandler script) {
        return getType(script, INPUT);
    }

    public static String getOutputType(ScriptHandler script) {
        return getType(script, OUTPUT);
    }

    private static String getType(ScriptHandler script, String propName) {
        Map properties = script.getProperties();
        Property prop = (Property) properties.get(propName);
        if (prop == null) {
            throw new IllegalStateException("'" + propName
                    + "' property not found in script " + script.getName()
                    + ".");
        }
        String type = prop.getType();
        return (type != null && type.length() != 0) ? type : null;
    }
}
