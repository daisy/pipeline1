/*
 * Created on 2005-mar-14
 */
package org.daisy.util.exception;


/**
 * @author LINUSE
 */
public class ValidationException extends BaseException {

	public ValidationException(String a_message) {
		super(a_message);
	}

	public ValidationException(String a_message, Throwable a_rootCause) {
		super(a_message, a_rootCause);
	}

}
