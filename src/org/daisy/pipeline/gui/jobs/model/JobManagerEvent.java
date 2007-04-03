package org.daisy.pipeline.gui.jobs.model;

import java.util.EventObject;

import org.daisy.dmfc.core.script.Job;

public class JobManagerEvent extends EventObject {

    private static final long serialVersionUID = 19477162373305880L;

    public enum Type {
        ADD, REMOVE, UPDATE;
    }

    private Job[] jobs;
    private Type type;
    private int index;

    public JobManagerEvent(Object source, Job[] jobs, Type type) {
        super(source);
        this.jobs = jobs;
        this.type = type;
        this.index = -1;
    }
    
    public JobManagerEvent(Object source, Job[] jobs, int index,  Type type) {
        super(source);
        this.jobs = jobs;
        this.type = type;
        this.index = index;
    }

    /**
     * @return the jobs that changed
     */
    public Job[] getJobs() {
        return jobs;
    }

    /**
     * @return the index at which the changes started
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * @return the type of the change
     */
    public Type getType() {
        return type;
    }

}
