package org.daisy.pipeline.gui.jobs.model;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.pipeline.gui.jobs.Messages;

/**
 * 
 * <p>Business object containing Scripthandler and parameters from GUI
 * Parameters are the input file(s(sets)) and output file(s(sets))
 * entered by the user.</p>
 * <p>All functionality of the ScriptHandler scripts are available
 * through the ScriptHandler object.</p>
 * 
 * @author Laurie Sherve
 * @author Linus Ericson
 * @author Romain Deltour
 *
 */
public class Job {

    private File inputFile;
    private File outputFile;
    private Status status = Status.WAITING;
    private final ScriptHandler script;
    
    public enum Status {
        WAITING,RUNNING, COMPLETED, FAILED;

        public String getLocalizedString() {
            return Messages.getString("Job.Status."+toString());
        }        
    }
    
    public Job(ScriptHandler script) {
        super();
        this.script = script;
    }

    public File getInputFile() {
        return inputFile;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public ScriptHandler getScript() {
        return script;
    }

    public Status getStatus() {
        return status;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }
    
    public void execute() throws ScriptException {
        status = Status.RUNNING;
        try {
            script.execute();
            status = Status.COMPLETED;
        } catch (Exception e) {
            status = Status.FAILED;
            if (e instanceof ScriptException) {
                throw (ScriptException) e;
            } else {
                throw new Error(e);
            }
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }
    
    

}
