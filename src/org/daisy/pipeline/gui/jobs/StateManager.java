package org.daisy.pipeline.gui.jobs;

import java.util.EventObject;

import org.daisy.dmfc.core.event.BusListener;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.ProgressChangeEvent;
import org.daisy.dmfc.core.event.ScriptStateChangeEvent;
import org.daisy.dmfc.core.event.StateChangeEvent;
import org.daisy.dmfc.core.event.TransformerProgressChangeEvent;
import org.daisy.dmfc.core.event.TransformerStateChangeEvent;
import org.daisy.dmfc.core.event.StateChangeEvent.Status;
import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.transformer.Transformer;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.pipeline.gui.jobs.model.JobManager;
import org.daisy.pipeline.gui.tasks.TaskInfo;
import org.daisy.util.execution.State;

/**
 * @author Romain Deltour
 * 
 */
public class StateManager implements BusListener {
    private static StateManager instance = new StateManager();
    // TODO check null/thread safety
    // private Object lock = new Object();
    private JobInfo runningJob;
    private TaskInfo runningTask;

    private StateManager() {
    }

    public static StateManager getInstance() {
        return instance;
    }

    public void init() {
        EventBus.getInstance().subscribe(this, ProgressChangeEvent.class);
        EventBus.getInstance().subscribe(this, StateChangeEvent.class);
    }

    public void dispose() {
        EventBus.getInstance().unsubscribe(this, ProgressChangeEvent.class);
        EventBus.getInstance().unsubscribe(this, StateChangeEvent.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.dmfc.core.event.BusListener#recieved(java.util.EventObject)
     */
    public void recieved(EventObject event) {
        if (event instanceof TransformerProgressChangeEvent) {
            TransformerProgressChangeEvent tpce = (TransformerProgressChangeEvent) event;
            progressChanged((Transformer) tpce.getSource(), tpce.getProgress());
        }
        if (event instanceof TransformerStateChangeEvent) {
            TransformerStateChangeEvent tce = (TransformerStateChangeEvent) event;
            stateChanged((Transformer) tce.getSource(), tce.getState());
        }
        if (event instanceof ScriptStateChangeEvent) {
            ScriptStateChangeEvent ssce = (ScriptStateChangeEvent) event;
            stateChanged((Job) ssce.getSource(), ssce.getState());
        }
    }

    /**
     * @param transformer
     * @param progress
     */
    private void progressChanged(Transformer transformer, double progress) {
        runningTask.setProgress(progress);
    }

    /**
     * @param transformer
     * @param state
     */
    private void stateChanged(Transformer transformer, Status status) {
        switch (status) {
        case STARTED:
            runningTask = getTask(transformer);
            runningTask.setState(State.RUNNING);
            break;
        case STOPPED:
            runningTask.setState(State.FINISHED);
            runningTask = null;
            break;
        default:
            break;
        }
    }

    private void stateChanged(Job job, Status state) {
        // TODO handle abort/failure
        switch (state) {
        case STARTED:
            runningJob = JobManager.getInstance().get(job);
            runningJob.setState(State.RUNNING);
            break;
        case STOPPED:
            runningJob.setState(State.FINISHED);
            runningJob = null;
            runningTask = null;
            break;
        default:
            break;
        }
    }

    private TaskInfo getTask(Transformer trans) {
        String name = trans.getTransformerInfo().getName();
        if (runningTask != null && runningTask.getName().equals(name)) {
            return runningTask;
        }
        for (TaskInfo info : runningJob.getTasks()) {
            if (info.getName().equals(name)) {
                return info;
            }
        }
        return null;
    }

}
