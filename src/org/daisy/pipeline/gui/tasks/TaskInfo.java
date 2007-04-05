package org.daisy.pipeline.gui.tasks;

import java.util.Collection;
import java.util.HashSet;

import org.daisy.dmfc.core.transformer.TransformerInfo;
import org.daisy.util.execution.State;

/**
 * @author Romain Deltour
 * 
 */
public class TaskInfo {

    private String name;
    private String description;
    private double progress;
    private State state;
    private long totalTime;
    private long startTime;
    private long elapsedTime;
    private long leftTime;

    /**
     * @param transInfo
     */
    public TaskInfo(TransformerInfo info) {
        this.name = info.getName();
        this.description = info.getDescription();
        this.progress = 0;
        this.state = State.IDLE;
    }

    public String getDescription() {
        return description;
    }

    public Collection getDocumentation() {
        return new HashSet();
    }

    public String getName() {
        return name;
    }

    public double getProgress() {
        return progress;
    }

    public State getSate() {
        return state;
    }

    public void setProgress(double progress) {
        this.progress = progress;
        setTimes();
    }

    public void setState(State state) {
        switch (state) {
        case RUNNING: 
            startTime = System.nanoTime();
            break;
        case FINISHED:     
            totalTime = System.nanoTime()-startTime;
            break;
        default:
            break;
        }
        this.state = state;
    }

    public long getLeftTime() {
        return leftTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    private void setTimes() {
        long now = System.nanoTime();
        totalTime = (long) ((now - startTime) / progress);
        elapsedTime = now - startTime;
        leftTime = totalTime - elapsedTime;
    }
}
