package org.daisy.util.fileset.exception;

import org.daisy.util.exception.BaseException;
import org.daisy.util.fileset.interfaces.FilesetFile;

/**
 * <p>A wrapper for any exception reported through the FilesetErrorHandler interface</p>
 * <p>This class is extended with subclasses declaring error severity.</p>
 * @see FilesetFileErrorException
 * @see FilesetFileFatalErrorException 
 * @see FilesetFileWarningException
 * @author Markus Gylling 
 */

public class FilesetFileException extends BaseException {
	private FilesetFile owner;
	private Throwable outercause;
	
	public FilesetFileException(FilesetFile origin, Throwable exc) {
		super("Fileset exception:", exc);
		this.outercause = exc;
		this.owner = origin;
	}

	/**
	 * @return the actual exception that occured
	 */
	public Throwable getCause() {
		return this.outercause;
	}
	
	/**
	 * @return the FilesetFile instance in which the exception occured
	 */
	public FilesetFile getOrigin(){
		return this.owner;
	}
	
	private static final long serialVersionUID = -2070247113922085583L;
	
}
