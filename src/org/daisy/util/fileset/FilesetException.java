package org.daisy.util.fileset;

import org.daisy.util.exception.BaseException;

/**
 * Wraps all nonrecoverable exceptions thrown by this package
 * @author Markus Gylling 
 */

public class FilesetException extends BaseException {

	public FilesetException(String message) {
		super(message);
	}

	public FilesetException(String message, Throwable cause) {
		super(message, cause);
	}

	public FilesetException(Throwable cause) {
		super("Fileset exception:", cause);
	}

}
