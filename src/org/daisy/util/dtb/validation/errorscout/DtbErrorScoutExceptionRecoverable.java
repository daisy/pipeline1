package org.daisy.util.dtb.validation.errorscout;

import org.daisy.util.exception.BaseException;

/**
 * A wrapper for recoverable exceptions (typically DTB validation error reports) encountered during scouting
 * @author Markus Gylling
 */
public class DtbErrorScoutExceptionRecoverable extends BaseException {

	public DtbErrorScoutExceptionRecoverable(String message) {
		super(message);
	}

	public DtbErrorScoutExceptionRecoverable(String message, Throwable cause) {
		super(message, cause);
	}

	public DtbErrorScoutExceptionRecoverable(Throwable cause) {
		super("DtbErrorScout error:", cause);
	}
}
