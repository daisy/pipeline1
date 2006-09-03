package org.daisy.util.fileset.validation.message;

import java.net.URI;

/**
 * Message issued when a Validator encounters an invalid state in the input fileset
 * that is flagged to have a severity level of <em>severe error</em>. 
 * @author Markus Gylling
 */
public class ValidatorSevereErrorMessage extends ValidatorMessage{

	public ValidatorSevereErrorMessage(URI file, String message, int line, int column) {
		super(file, message, line, column);
	}

	public ValidatorSevereErrorMessage(URI file, String message) {
		super(file, message);
	}
}
