package se_tpb_aligner.align;

import org.daisy.util.exception.BaseException;

/**
 * An exception thrown when an aligner fails to do its job.
 * @author Markus Gylling
 */
public class AlignerException extends BaseException {
	
	public AlignerException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlignerException(String message) {
		super(message);
	}
	
	private static final long serialVersionUID = -7327281996383400519L;

}
