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

/**
 * Base class for exceptions
 * @author Linus Ericson
 */
public class BaseException extends Exception {
	
	protected Throwable rootCause = null;
	
	/**
	 * Creates a new BaseException
	 * @param a_message a description of the exception
	 */
	public BaseException(String a_message) {
		super(a_message);
	}
	
	/**
	 * Creates a new BaseException
	 * @param a_message a description of the exception
	 * @param a_rootCause the root cause of the exception
	 */
	public BaseException(String a_message, Throwable a_rootCause) {
		super(a_message);
		rootCause = a_rootCause;
	}
	
	/**
	 * Sets the root cause of this exception.
	 * @param a_rootCause the root cause of the exception
	 */
	public void setRootCause(Throwable a_rootCause) {
		rootCause = a_rootCause;
	}
	
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	public void printStackTrace(PrintStream a_outStream) {
		printStackTrace(new PrintWriter(a_outStream));
	}

	public void printStackTrace(PrintWriter a_writer) {
		super.printStackTrace(a_writer);
		if(rootCause != null) {
			rootCause.printStackTrace(a_writer);
		}
		a_writer.flush();
	}

	public String getRootCauseMessages() {
	    StringBuffer _buffer = new StringBuffer();
	    Throwable _thr = rootCause;
	    while (_thr != null) {
	        try {
                BaseException _base = (BaseException)_thr;
                _buffer.append(_base.getMessage() + "\n\t");
                _thr = _base.getRootCause();
            } catch (ClassCastException e) {
                _buffer.append(_thr.getMessage());
                _thr = null;
            }
	    }
	    return _buffer.toString();
	}
	
	/**
	 * @return Returns the rootCause.
	 */
	public Throwable getRootCause() {	    
		return rootCause;
	}

}
