package se_tpb_aligner.align;

import org.daisy.util.exception.BaseException;

/**
 * An exception thrown when an aligner factory fails to produce an instance of aligner
 * @author Markus Gylling
 */
public class AlignerFactoryException extends BaseException {

	public AlignerFactoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlignerFactoryException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -6621889294734301991L;

}
