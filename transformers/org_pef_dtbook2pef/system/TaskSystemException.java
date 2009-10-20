package org_pef_dtbook2pef.system;

/**
 * Exception for the TaskSystem interface.
 * @author joha
 *
 */
public class TaskSystemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 45773873175031980L;

	public TaskSystemException() {
		super();
	}

	public TaskSystemException(String message) {
		super(message);
	}

	public TaskSystemException(Throwable cause) {
		super(cause);
	}

	public TaskSystemException(String message, Throwable cause) {
		super(message, cause);
	}

}
