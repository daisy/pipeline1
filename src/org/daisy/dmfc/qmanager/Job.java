package org.daisy.dmfc.qmanager;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptHandler;

public class Job {

    private File input;
    private File output;
    private int status = Status.WAITING;
    private ScriptHandler script;
    
    public Job(File input, File output, int status, ScriptHandler script) {
        super();
        this.input = input;
        this.output = output;
        this.status = status;
        this.script = script;
    }

    public int getStatus() {
        return status;
    }

    public ScriptHandler getScript() {
        return script;
    }

    public File getInputFile() {
        return input;
    }

    public File getOutputFile() {
        return output;
    }

}
