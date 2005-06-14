package org.daisy.util.fileset;

import org.daisy.util.exception.BaseException;

/**
 * Wraps recoverable exceptions thrown by this package
 * @author Markus Gylling 
 */

public class FilesetExceptionRecoverable extends BaseException {

	public FilesetExceptionRecoverable(String message) {
		super(message);
	}

	public FilesetExceptionRecoverable(String message, Throwable cause) {
		super(message, cause);
	}

	public FilesetExceptionRecoverable(Throwable cause) {
		super("Fileset recoverable exception:", cause);
	}

}
