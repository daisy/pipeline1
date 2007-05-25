/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
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
                    "Cannot set progress: state is not RUNNING"); //$NON-NLS-1$
        }
        if (progress < 0 || progress > 1) {
            throw new IllegalArgumentException("Progress out of bounds"); //$NON-NLS-1$
        }
        this.progress = progress;
        timer.update(progress);
    }

    public synchronized void setState(State state) {
        switch (state) {
        case ABORTED:
            setAborted();
            break;
        case FAILED:
            setFailed();
            break;
        case FINISHED:
            setFinished();
            break;
        case IDLE:
            setIdle();
            break;
        case RUNNING:
            setRunning();
            break;
        case WAITING:
            setWaiting();
            break;
        default:
            checkState(false, null);
        }
        this.state = state;
    }

    private void checkState(boolean ok, State state) {
        if (!ok) {
            throw new IllegalStateException("Try to make " + state + " a " //$NON-NLS-1$ //$NON-NLS-2$
                    + this.state + ' ' + this.getClass().getName());
        }
    }

    protected void setAborted() {
        checkState(this.state == State.RUNNING, state);
        stoppedRunning();
    }

    protected void setFailed() {
        checkState(this.state == State.RUNNING, state);
        stoppedRunning();
    }

    protected void setFinished() {
        checkState(this.state == State.RUNNING, state);
        progress = 1.0;
        stoppedRunning();
    }

    protected void setIdle() {
        checkState(this.state != State.RUNNING, state);
        timer.reset();
    }

    protected void setRunning() {
        checkState(this.state == State.WAITING, state);
        timer.start();
    }

    protected void setWaiting() {
        checkState(this.state == State.IDLE, state);
    }

    protected void stoppedRunning() {
        timer.stop();
    }
}
