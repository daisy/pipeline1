package org.daisy.pipeline.gui.util;

import org.daisy.util.execution.State;

/**
 * @author Romain Deltour
 * 
 */
public class StateObject {

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
    public StateObject(String name, String description) {
        this.name = name;
        this.description = description;
        this.progress = 0;
        this.state = State.IDLE;
    }

    public String getDescription() {
        return description;
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
        if (state != State.RUNNING) {
            throw new IllegalStateException(
                    "Cannot set progress: state is not RUNNING");
        }
        if (progress < 0 || progress > 1) {
            throw new IllegalArgumentException("Progress out of bounds");
        }
        this.progress = progress;
        setTimes();
    }

    public void setState(State state) {
        switch (state) {
        case RUNNING:
            startTime = System.nanoTime();
            break;
        case FINISHED:
            progress = 1.0;
            totalTime = System.nanoTime() - startTime;
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
        if (progress == 0) {
            return;
        }
        long now = System.nanoTime();
        totalTime = (long) ((now - startTime) / progress);
        elapsedTime = now - startTime;
        leftTime = totalTime - elapsedTime;
    }

    public static String format(long ns) {
        long tmp;
        tmp = ns / 1000000;
        long ms = (tmp) % 1000;
        tmp = tmp / 1000;
        long s = tmp % 60;
        tmp = s / 60;
        long m = tmp % 60;
        long h = m / 60;

        return "" + h + ':' + m + ':' + s + '.' + ms;
    }
}
