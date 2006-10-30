package org.daisy.util.fileset.encryption.exception;

/**
 * Wrapper Exception for any exception thrown by a public interface of the <code>fileset.encryption</code> package.
 * @author Markus Gylling
 */

import org.daisy.util.fileset.exception.FilesetFatalException;

public class EncryptionException extends FilesetFatalException {
	
	public EncryptionException(String message) {
		super(message);
	}

	public EncryptionException(String message, Throwable e) {
		super(message, e);
	}
	
	private static final long serialVersionUID = -3150266436305198789L;

}
