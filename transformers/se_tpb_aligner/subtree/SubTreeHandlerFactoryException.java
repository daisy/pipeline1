package se_tpb_aligner.subtree;

import org.daisy.util.exception.BaseException;

/**
 *
 * @author Markus Gylling
 */
public class SubTreeHandlerFactoryException extends BaseException {

	public SubTreeHandlerFactoryException(String message) {
		super(message);
	}

	public SubTreeHandlerFactoryException(String message, Exception e) {
		super(message,e);
	}

	private static final long serialVersionUID = -107350296022246702L;
	
}
