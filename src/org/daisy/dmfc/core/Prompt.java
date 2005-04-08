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
	// FIXME add message originator
    private String message = null;
    private Level level = null;
    private double progress = 0;
    private boolean progressReport = false;
    
    public Prompt(Level a_level, String a_message) {
        message = a_message;
        level = a_level;
    }
    
    public Prompt(double a_progress) {
        if (a_progress < 0 || a_progress > 100) {
            throw new IllegalArgumentException("Progress must be in the interval [0,100]. Was " + a_progress);
        }
        progress = a_progress;
        progressReport = true;
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
     * @return Returns the progressReport.
     */
    public boolean isProgressReport() {
        return progressReport;
    }
}
