package org_pef_dtbook2pef.system.tasks.layout.flow;

/**
 * A LayoutException is an exception that indicates 
 * conditions in the layout process that a reasonable 
 * application might want to catch.
 * @author Joel Håkansson, TPB
 */
public class LayoutException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2908554164728732775L;

	public LayoutException() { }

	public LayoutException(String message) {
		super(message);
	}

	public LayoutException(Throwable cause) {
		super(cause);
	}

	public LayoutException(String message, Throwable cause) {
		super(message, cause);
	}

}
