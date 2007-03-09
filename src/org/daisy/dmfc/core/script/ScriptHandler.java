package org.daisy.dmfc.core.script;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScriptHandler {

    private String description = "This is a script";

    private String name = "Script";

    private Map properties = new HashMap();

    private List tasks = new LinkedList();

    private List transformerInfoList = new LinkedList();

    private int currentTaskIndex = 0;

    @SuppressWarnings("unchecked")
    public ScriptHandler() {
        super();
        properties.put("input", new Property("input","FXIME","application/xml"));
        properties.put("outputPath", new Property("outputPath","FXIME","application/xml"));
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Map getProperties() {
        return properties;
    }

    public List getTasks() {
        return tasks;
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public List getTransformerInfoList() {
        return transformerInfoList;
    }

    public int getCurrentTaskIndex() {
        return currentTaskIndex;
    }
}
