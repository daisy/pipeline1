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
    
    /**
     * Creates a simple text message.
     * @param a_level the level of the message
     * @param a_message the message itself
     * @param a_originator the originator of the message
     */
    public Prompt(Level a_level, String a_message, String a_originator) {
        message = a_message;
        level = a_level;
        messageOriginator = a_originator;
        type = Prompt.MESSAGE;
    }
    
    /**
     * Report Transformer progress.
     * @param a_progress 
     * @param a_originator the originator of the message
     */
    public Prompt(double a_progress, String a_originator) {
        if (a_progress < 0 || a_progress > 100) {
            throw new IllegalArgumentException("Progress must be in the interval [0,100]. Was " + a_progress);
        }
        progress = a_progress;
        messageOriginator = a_originator;
        type = Prompt.PROGRESS;
    }

    /**
     * Report when a Transformer has started or finished running.
     * @param a_start <code>true</code> if the Transformer has just been started, <code>false</code> otherwise
     * @param a_originator the name of the Transformer that has started/finished.
     */
    public Prompt(boolean a_start, String a_originator) {
        messageOriginator = a_originator;
        if (a_start) {
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
}
