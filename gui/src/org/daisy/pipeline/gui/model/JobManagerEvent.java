package org.daisy.pipeline.gui.model;

import java.util.EventObject;

public class JobManagerEvent extends EventObject {

    private static final long serialVersionUID = 19477162373305880L;

    public enum Type {
        ADD, REMOVE, UPDATE;
    }

    private JobInfo[] jobs;
    private Type type;
    private int index;

    public JobManagerEvent(Object source, JobInfo[] jobs, Type type) {
        super(source);
        this.jobs = jobs;
        this.type = type;
        this.index = -1;
    }

    public JobManagerEvent(Object source, JobInfo[] jobs, int index, Type type) {
        super(source);
        this.jobs = jobs;
        this.type = type;
        this.index = index;
    }

    /**
     * @return the jobInfos that changed
     */
    public JobInfo[] getJobs() {
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
