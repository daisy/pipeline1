package org_pef_text.pef2text;

public class UnsupportedPaperException extends EmbosserFactoryException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4972964705499633760L;

	public UnsupportedPaperException() { }

	public UnsupportedPaperException(String message) {
		super(message);
	}

	public UnsupportedPaperException(Throwable cause) {
		super(cause);
	}

	public UnsupportedPaperException(String message, Throwable cause) {
		super(message, cause);
	}

}
