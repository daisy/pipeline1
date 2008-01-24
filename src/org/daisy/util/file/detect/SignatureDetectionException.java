package org.daisy.util.file.detect;

/**
 *
 * @author Markus Gylling
 */
public class SignatureDetectionException extends Exception {

	/*package*/ SignatureDetectionException(String message) {
		super(message);
	}
	
	/*package*/ SignatureDetectionException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 2880893548402049604L;
	
}
