package int_daisy_filesetRenamer;

import org.daisy.util.exception.BaseException;

/**
 * 
 * @author Markus Gylling
 */
public class FilesetRenamingException extends BaseException {

	public FilesetRenamingException(String message) {
		super(message);
	}
	
	public FilesetRenamingException(String message, Throwable cause) {
		super(message, cause);
	}

	private static final long serialVersionUID = -8737308465452478492L;

}
