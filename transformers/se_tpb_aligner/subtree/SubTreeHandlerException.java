package se_tpb_aligner.subtree;

import org.daisy.util.exception.BaseException;

/**
 *
 * @author Markus Gylling
 */
public class SubTreeHandlerException extends BaseException {

	public SubTreeHandlerException(String message) {
		super(message);
	}

	public SubTreeHandlerException(String message, Exception e) {
		super(message,e);
	}

	private static final long serialVersionUID = -107350296022246702L;
	
}
