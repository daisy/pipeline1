/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
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
package org.daisy.dmfc.core;

import java.util.logging.Level;

import org.daisy.util.fileset.SmilClock;

/**
 * @author Linus Ericson
 */
public class Prompt {
    public static final int MESSAGE = 0;
    public static final int PROGRESS = 1;
    public static final int TRANSFORMER_START = 2;
    public static final int TRANSFORMER_END = 3;
    
    private String message = null;
    private Level level = null;
    private double progress = 0;
    private String messageOriginator = "";
    private int type = Prompt.MESSAGE;
    private SmilClock totalTime = null;
    private SmilClock timeLeft = null;
    
    /**
     * Creates a simple text message.
     * @param lvl the level of the message
     * @param msg the message itself
     * @param originator the originator of the message
     */
    public Prompt(Level lvl, String msg, String originator) {
        message = msg;
        level = lvl;
        messageOriginator = originator;
        type = Prompt.MESSAGE;
    }
    
    /**
     * Report Transformer progress.
     * @param prgrs 
     * @param originator the originator of the message
     */
    public Prompt(double prgrs, long startTime, String originator) {
        if (prgrs < 0 || prgrs > 1) {
            throw new IllegalArgumentException("Progress must be in the interval [0,1]. Was " + prgrs);
        }
        long now = System.currentTimeMillis();
        if (prgrs > 0) {
            totalTime = new SmilClock((long)((double)(now - startTime) / prgrs));
            timeLeft = new SmilClock((long)(totalTime.millisecondsValue() - (now - startTime)));
        }
        progress = prgrs;
        messageOriginator = originator;
        type = Prompt.PROGRESS;
    }

    /**
     * Report when a Transformer has started or finished running.
     * @param start <code>true</code> if the Transformer has just been started, <code>false</code> otherwise
     * @param originator the name of the Transformer that has started/finished.
     */
    public Prompt(boolean start, String originator) {
        messageOriginator = originator;
        if (start) {
            type = Prompt.TRANSFORMER_START;
        } else {
            type = Prompt.TRANSFORMER_END;
        }
    }
    
    /**
     * @return Returns the level.
     */
    public Level getLevel() {
        return level;
    }
    
    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @return Returns the progress.
     */
    public double getProgress() {
        return progress;
    }    
    
    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }
    
    /**
     * @return Returns the messageOriginator.
     */
    public String getMessageOriginator() {
        return messageOriginator;
    }
    
    public SmilClock getTotalTime() {
        return totalTime;
    }
    public SmilClock getTimeLeft() {
        return timeLeft;
    }
}
