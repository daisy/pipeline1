/**
 * 
 */
package org.daisy.util.dtb.build;

import org.daisy.util.exception.BaseException;

/**
 * Base class for all exceptions thrown by DTB file builders
 * @author jpritchett@rfbd.org
 *
 */
public class BuildException extends BaseException {

	/**
	 * @param message
	 */
	public BuildException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BuildException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = -1643519307194672953L;
}
