package org.daisy.pipeline.gui.util;

import org.daisy.util.execution.State;

/**
 * @author Romain Deltour
 * 
 */
public class StateTracker {

    private String name;
    private State state;
    private double progress;
    private Timer timer;

    /**
     * @param transInfo
     */
    public StateTracker(String name) {
        this.name = name;
        this.state = State.IDLE;
        this.progress = 0;
        this.timer = new Timer();
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

    public Timer getTimer() {
        return timer;
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
        timer.update(progress);
    }

    public synchronized void setState(State state) {
        switch (state) {
        case ABORTED:
            checkState(this.state == State.RUNNING, state);
            timer.stop();
            break;
        case FAILED:
            checkState(this.state == State.RUNNING, state);
            timer.stop();
            break;
        case FINISHED:
            checkState(this.state == State.RUNNING, state);
            progress = 1.0;
            timer.stop();
            break;
        case IDLE:
            checkState(this.state != State.RUNNING, state);
            timer.reset();
            break;
        case RUNNING:
            checkState(this.state == State.WAITING, state);
            timer.start();
            break;
        case WAITING:
            checkState(this.state == State.IDLE, state);
            break;
        default:
            checkState(false, null);
        }
        this.state = state;
    }

    private void checkState(boolean ok, State state) {
        if (!ok) {
            throw new IllegalStateException("Try to make " + state + " a "
                    + this.state +' '+ this.getClass().getName());
        }
    }
}
