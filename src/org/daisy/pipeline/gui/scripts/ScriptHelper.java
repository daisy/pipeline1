package org.daisy.pipeline.gui.scripts;

import org.daisy.dmfc.core.script.Script;

public final class ScriptHelper {

    private final static String INPUT = "input";
    private final static String OUTPUT = "outputPath";

    private ScriptHelper() {
        // Nothing. This class is a static utility.
    }

    public static boolean isOutputRequired(Script script) {
        // TODO implem isOutputRequired(script)
        return true;
    }

    public static String getInputType(Script script) {
        return getType(script, INPUT);
    }

    public static String getOutputType(Script script) {
        return getType(script, OUTPUT);
    }

    private static String getType(Script script, String propName) {
        // TODO implem getType()
        // Map properties = script.getProperties();
        // Property prop = (Property) properties.get(propName);
        // if (prop == null) {
        // throw new IllegalStateException("'" + propName
        // + "' property not found in script " + script.getName()
        // + ".");
        // }
        // String type = prop.getType();
        // return (type != null && type.length() != 0) ? type : null;
        return "";
    }
}
