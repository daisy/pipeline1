package se_tpb_aligner.textpre;

import org.daisy.util.exception.BaseException;

/**
 * An exception thrown when an aligner factory fails to produce an instance of aligner
 * @author Markus Gylling
 */
public class PreProcessorFactoryException extends BaseException {

	public PreProcessorFactoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public PreProcessorFactoryException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -6621889294734301991L;

}
