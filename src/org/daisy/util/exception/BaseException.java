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
package org.daisy.util.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * Base class for exceptions
 * @author Linus Ericson
 */
public class BaseException extends Exception {
	
	protected Throwable rootCause = null;
	
	/**
	 * Creates a new BaseException
	 * @param message a description of the exception
	 */
	public BaseException(String message) {
		super(message);
	}
	
	/**
	 * Creates a new BaseException
	 * @param message a description of the exception
	 * @param cause the root cause of the exception
	 */
	public BaseException(String message, Throwable cause) {
		super(message);
		rootCause = cause;
	}
	
	/**
	 * Sets the root cause of this exception.
	 * @param cause the root cause of the exception
	 */
	public void setRootCause(Throwable cause) {
		rootCause = cause;
	}
	
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream outStream) {
		printStackTrace(new PrintWriter(outStream));
	}

	public void printStackTrace(PrintWriter writer) {
		super.printStackTrace(writer);
		if(rootCause != null) {
			rootCause.printStackTrace(writer);
		}
		writer.flush();
	}

	/**
	 * Get all root cause messages.
	 * @return an array of root cause messages
	 */
	public String[] getRootCauseMessages() {
	    Vector vec = new Vector();
	    Throwable thr = rootCause;
	    while (thr != null) {
	        try {
                BaseException base = (BaseException)thr;
                vec.add(base.getMessage());
                thr = base.getRootCause();
            } catch (ClassCastException e) {
                vec.add(thr.getMessage());
                thr = null;
            }
	    }
	    int count = vec.size();
	    String[] messages = new String[count];
	    vec.copyInto(messages);
	    return messages;
	}
	
	/**
	 * Gets a string representation of the array returned by
	 * getRootCauseMessages
	 * @return a string of root cause messages
	 */
	public String getRootCauseMessagesAsString() {
	    StringBuffer buffer = new StringBuffer();
	    String[] msgs = getRootCauseMessages();
	    if (msgs.length > 0) {
	        buffer.append(msgs[0]);	    
		    for (int i = 1; i < msgs.length; ++i) {
		        buffer.append(", caused by " + msgs[i]);
		    }
	    }
	    return buffer.toString();
	}
	
	/**
	 * @return Returns the rootCause.
	 */
	public Throwable getRootCause() {	    
		return rootCause;
	}

}
