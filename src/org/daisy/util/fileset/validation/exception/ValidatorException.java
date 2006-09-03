package org.daisy.util.fileset.validation.exception;

import org.daisy.util.exception.BaseException;

/**
 * @author Markus Gylling
 */
public class ValidatorException extends	BaseException {

	public ValidatorException(String message) {
		super(message);
	}
	
	public ValidatorException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = -1491692757313782711L;

}
