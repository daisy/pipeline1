/*
 * Created on 2005-jun-17
 */
package org.daisy.util.fileset;

import org.daisy.util.exception.BaseException;

/**
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

	private static final long serialVersionUID = 5400731112977853555L;
}
