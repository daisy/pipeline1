package org.daisy.util.xml.pool;

import org.daisy.util.exception.BaseException;

public class PoolException extends BaseException {
	
	public PoolException(String message) {
		super(message);	
	}	
	
	public PoolException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = 4680103539569884706L;

}
