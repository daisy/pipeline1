/*
 * Created on 2005-mar-10
 */
package org.daisy.util.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Base class for exceptions
 * @author LINUSE
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

	/**
	 * @return Returns the rootCause.
	 */
	public Throwable getRootCause() {
		return rootCause;
	}

}
