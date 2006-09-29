package se_tpb_speechgenerator;

/**
 * Exception thrown by TTSBuilder.
 * @author Martin Blomberg
 *
 */
public class TTSBuilderException extends Exception {
	
	/**
	 * @param message
	 */
	public TTSBuilderException(String message) {
		super(message);
	}
	
	/**
	 * @param message
	 * @param cause
	 */
	public TTSBuilderException(String message, Throwable cause) {
		super(message, cause);
	}
}
