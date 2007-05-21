package org.daisy.pipeline.gui.util;

import org.daisy.util.xml.SmilClock;

/**
 * @author Romain Deltour
 * 
 */
public class Timer {

    private long totalTime;
    private long startTime;
    private long elapsedTime;
    private long leftTime;
    private boolean running;

    public Timer() {
        running = false;
    }

    public static String format(long ns) {    	
    	SmilClock sc = new SmilClock(ns/1000000);
    	return sc.toString(SmilClock.FULL);
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

    public void reset() {
        running = false;
        startTime = 0;
        elapsedTime = 0;
        totalTime = 0;
        leftTime = 0;
    }

    public void start() {
        startTime = System.nanoTime();
        running = true;
    }

    public void stop() {
        totalTime = System.nanoTime() - startTime;
        running = false;
    }

    public void update(double progress) {
        if (!running || progress == 0) {
            return;
        }
        long now = System.nanoTime();
        totalTime = (long) ((now - startTime) / progress);
        elapsedTime = now - startTime;
        leftTime = totalTime - elapsedTime;
    }
}
