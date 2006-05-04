package org.daisy.util.fileset.exception;

import org.daisy.util.exception.BaseException;

/**
 * <p>Thrown if Fileset had fatal errors during population.</p>
 * <p>If this Exception is thrown, the Fileset instance did
 * not complete its instantiation phase and is null.</p>
 * @author Markus Gylling 
 */

public class FilesetFatalException extends BaseException {

	public FilesetFatalException(String message) {
		super(message);
	}

	public FilesetFatalException(String message, Throwable cause) {
		super(message, cause);
	}

	public FilesetFatalException(Throwable cause) {
		super("Fileset exception:", cause);
	}

	private static final long serialVersionUID = 5400731112977853555L;
}
