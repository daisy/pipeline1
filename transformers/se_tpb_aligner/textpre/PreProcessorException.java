package se_tpb_aligner.textpre;

import org.daisy.util.exception.BaseException;

/**
 * An exception thrown when an aligner fails to do its job.
 * @author Markus Gylling
 */
public class PreProcessorException extends BaseException {
	
	public PreProcessorException(String message, Throwable cause) {
		super(message, cause);
	}

	public PreProcessorException(String message) {
		super(message);
	}
	
	private static final long serialVersionUID = -7327281996383400519L;

}
