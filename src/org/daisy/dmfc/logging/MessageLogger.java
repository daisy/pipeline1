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
import java.util.EventObject;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dmfc.core.event.Event;
import org.daisy.dmfc.core.event.BusListener;
import org.daisy.dmfc.core.event.StateChangeEvent;

/**
 * @author Linus Ericson
 */
public class MessageLogger implements BusListener {
    
    private static Logger logger = Logger.getLogger("dmfc.logger");
    private Class<? extends EventObject> eventLevel = Event.class;
    
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
     * @param level only log messages of the given EventObject level in the Pipeline event inheritance hierarchy.
     * @param fileNamePattern the pattern of the filename. The pattern syntax is
     * described in the <code>FileHandler</code> documentation. 
     * @return <code>true</code> if the operation was successful, <code>false</code> otherwise
     * @see java.util.logging.FileHandler
     */
    public boolean addFileHandler(Class<? extends EventObject> level, String fileNamePattern) {
	    try {
	        Handler handler = new FileHandler(fileNamePattern);
	        Formatter formatter = new LineFormatter();
	        eventLevel = level;
	        handler.setFormatter(formatter);
	        handler.setLevel(Level.ALL);
	        logger.addHandler(handler);
	    } catch (IOException e) {
	        return false;
	    }
	    return true;
    }
       
    public boolean addFileHandler(Formatter formatter, Level level, String fileNamePattern, int limit, int count, boolean append) {
	    try {	        
	        Handler handler = new FileHandler(fileNamePattern, limit, count, append);	        
	        handler.setFormatter(formatter);
	        handler.setLevel(level);
	        logger.addHandler(handler);
	    } catch (IOException e) {
	        return false;
	    }
	    return true;
    }
    
    /**
     * Adds a ConsoleHandler to this logger.
     * @param level only log messages at level <code>a_level</code> or above.
     * @param formatter the formatter to use with the handler
     */
    public void addConsoleHandler(Formatter formatter, Level level) {    
        Handler handler = new ConsoleHandler();	        
        handler.setFormatter(formatter);
        handler.setLevel(level);
        logger.addHandler(handler);
	}
        
	public void recieved(EventObject event) {
		//TODO
		logger.info(event.toString());		
	}

}