package org.daisy.util.mime;

/**
 * 
 * @author Markus Gylling
 */
public class MIMETypeRegistryException extends MIMEException {

	public MIMETypeRegistryException(String message) {
		super(message);
	}
	
	public MIMETypeRegistryException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 5038899673796693024L;
}
