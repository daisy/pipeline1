/*
 * Created on 2005-mar-10
 */
package org.daisy.dmfc.exception;

import org.daisy.util.exception.*;

/**
 * Thrown when a Transformer as been disabled. The reason for the exception
 * being thrown might be found in the root cause.
 * @author LINUSE
 */
public class TransformerDisabledException extends BaseException {

	public TransformerDisabledException(String a_message) {
		super(a_message);
	}
		
	public TransformerDisabledException(String a_message, Throwable a_rootCause) {
		super(a_message, a_rootCause);
	}
}
