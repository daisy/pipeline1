/*
 * Created on 2005-mar-18
 */
package org.daisy.dmfc.exception;

import org.daisy.util.exception.*;

/**
 * @author LINUSE
 */
public class ScriptException extends BaseException {

	public ScriptException(String a_message) {
		super(a_message);
	}

	public ScriptException(String a_message, Throwable a_rootCause) {
		super(a_message, a_rootCause);
	}

}
