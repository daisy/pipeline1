/*
 * Created on 2005-mar-10
 */
package org.daisy.dmfc.exception;

import org.daisy.util.exception.*;

/**
 * @author LINUSE
 */
public class TransformerRunException extends BaseException {

	public TransformerRunException(String a_message) {
		super(a_message);
	}

	public TransformerRunException(String a_message, Throwable a_rootCause) {
		super(a_message, a_rootCause);
	}

}
