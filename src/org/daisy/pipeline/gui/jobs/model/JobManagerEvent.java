package org.daisy.pipeline.gui.jobs.model;

import java.util.EventObject;

public class JobManagerEvent extends EventObject {

    private static final long serialVersionUID = 19477162373305880L;

    public enum Type {
        ADD, REMOVE, UPDATE;
    }

    private Job[] jobs;
    private Type type;

    public JobManagerEvent(Object source, Job[] jobs, Type type) {
        super(source);
        this.jobs = jobs;
        this.type = type;
    }

    /**
     * @return the jobs that changed
     */
    public Job[] getJobs() {
        return jobs;
    }

    /**
     * @return the type of the change
     */
    public Type getType() {
        return type;
    }

}
