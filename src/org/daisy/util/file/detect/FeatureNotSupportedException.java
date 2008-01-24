package org.daisy.util.file.detect;

/**
 *
 * @author Markus Gylling
 */
public class FeatureNotSupportedException extends Exception {

	/*package*/ FeatureNotSupportedException(String message) {
		super(message);
	}
	
	/*package*/ FeatureNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 2880893548402049604L;
	
}
