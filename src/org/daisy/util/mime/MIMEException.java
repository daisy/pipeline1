package org.daisy.util.mime;

import org.daisy.util.exception.BaseException;

/**
 * Base class for all Exceptions thrown from org.daisy.util.mime
 * @author Markus Gylling
 */
public class MIMEException extends BaseException {

	public MIMEException(String message) {
		super(message);
	}
	
	public MIMEException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = -5697745475995648615L;
}
