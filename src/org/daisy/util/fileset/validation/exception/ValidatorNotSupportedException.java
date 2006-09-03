package org.daisy.util.fileset.validation.exception;

/**
 * @author Markus Gylling
 */
public class ValidatorNotSupportedException extends ValidatorException {

	public ValidatorNotSupportedException(String message) {
		super(message);
	}

	public ValidatorNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	private static final long serialVersionUID = -8878257213624083203L;
}
