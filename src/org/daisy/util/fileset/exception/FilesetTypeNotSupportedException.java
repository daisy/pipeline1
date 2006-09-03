package org.daisy.util.fileset.exception;

/**
 * 
 * @author Markus Gylling
 */
public class FilesetTypeNotSupportedException extends FilesetFatalException {

	public FilesetTypeNotSupportedException(String message) {
		super(message);
	}

	public FilesetTypeNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public FilesetTypeNotSupportedException(Throwable cause) {
		super(cause);
	}

	private static final long serialVersionUID = -8957151438935187834L;
}
