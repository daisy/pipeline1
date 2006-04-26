package org.daisy.util.mime;

/**
 * 
 * @author Markus Gylling
 */
public class MIMETypeException extends MIMEException {

	public MIMETypeException(String message) {
		super(message);
	}
	
	public MIMETypeException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = -320032105573303494L;
}
