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

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.MessageEvent;

/**
 * A log handler that writes the log events into dmfc.
 * @author Linus Ericson
 */
public class LogHandler extends Handler {

    
    
    public LogHandler() {
        
    }
    
    public void publish(LogRecord record) {
       EventBus.getInstance().publish(
    		   new MessageEvent(
    				   this,"[LogHandler] " + record.getMessage(),MessageEvent.Type.ERROR));
                     
    }

    public void flush() {

    }

    public void close() throws SecurityException {

    }

}
