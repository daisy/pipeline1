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
