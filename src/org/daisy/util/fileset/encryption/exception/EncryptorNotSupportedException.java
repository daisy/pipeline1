package org.daisy.util.fileset.encryption.exception;

/**
 * Thrown when an Encryptor or an EncryptorFactory can not provide requested services.
 * @author Markus Gylling
 */
public class EncryptorNotSupportedException extends EncryptionException {

	public EncryptorNotSupportedException(String message) {
		super(message);
	}

	public EncryptorNotSupportedException(String message, Throwable e) {
		super(message, e);
	}
	
	private static final long serialVersionUID = -1941705696971845104L;

}
