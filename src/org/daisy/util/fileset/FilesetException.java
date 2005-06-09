package org.daisy.util.fileset;

/**
 * Base class for all exceptions thrown by this package
 * @author Markus Gylling 
 */

public class FilesetException extends Exception {

	public FilesetException() {
		super();
	}

	public FilesetException(String message) {
		super(message);
	}

	public FilesetException(String message, Throwable cause) {
		super(message, cause);
	}

	public FilesetException(Throwable cause) {
		super(cause);
	}
}
