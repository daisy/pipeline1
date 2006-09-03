package org.daisy.util.fileset.validation.exception;

/**
 * @author Markus Gylling
 */
public class ValidatorNotRecognizedException extends ValidatorException {

	public ValidatorNotRecognizedException(String message) {
		super(message);
	}

	public ValidatorNotRecognizedException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 1321811719050015633L;
}
