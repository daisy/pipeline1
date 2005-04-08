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
package org.daisy.dmfc.logging;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.Prompt;

/**
 * @author Linus Ericson
 */
public class MessageLogger implements EventListener {

    private static Logger logger = Logger.getLogger("dmfc.logger");
    
    static {
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
    }
    
    /**
     * Creates a logger without any handlers.
     */
    public MessageLogger() {
        
    }

    /**
     * Adds a FileHandler to this logger.
     * @param a_level only log messages at level <code>a_level</code> or above.
     * @param a_fileNamePattern the pattern of the filename. The pattern syntax is
     * described in the <code>FileHandler</code> documentation. 
     * @return <code>true</code> if the operation was successful, <code>false</code> otherwise
     * @see java.util.logging.FileHandler
     */
    public boolean addFileHandler(Level a_level, String a_fileNamePattern) {
	    try {	        
	        Handler _handler = new FileHandler(a_fileNamePattern);
	        Formatter _formatter = new LineFormatter();
	        _handler.setFormatter(_formatter);
	        _handler.setLevel(a_level);
	        logger.addHandler(_handler);
	    } catch (IOException e) {
	        return false;
	    }
	    return true;
    }
    
    /**
     * Adds a ConsoleHandler to this logger.
     * @param a_level only log messages at level <code>a_level</code> or above.
     */
    public void addConsoleHandler(Level a_level) {    
        Handler _handler = new ConsoleHandler();	        
        Formatter _formatter = new LineFormatter();
        _handler.setFormatter(_formatter);
        _handler.setLevel(a_level);
        logger.addHandler(_handler);
	}
        
    public void message(Prompt a_prompt) {
        if (a_prompt.isProgressReport()) {
            logger.info("Progress: " + a_prompt.getProgress());
        } else {
            logger.log(a_prompt.getLevel(), a_prompt.getMessage());
        }
    }

}
