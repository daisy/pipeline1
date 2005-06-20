package org.daisy.util.dtb.validation.errorscout;

import org.daisy.util.exception.BaseException;

/**
 * Wrapper for nonrecoverable exceptions raised during initialization or scouting
 * @author Markus Gylling
 */
public class DtbErrorScoutException extends BaseException {

	public DtbErrorScoutException(String message) {
		super(message);
	}

	public DtbErrorScoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public DtbErrorScoutException(Throwable cause) {
		super("Nonrecoverable DtbErrorScout exception:", cause);
	}
	
}
