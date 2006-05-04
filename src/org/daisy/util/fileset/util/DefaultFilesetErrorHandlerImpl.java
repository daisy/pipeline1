package org.daisy.util.fileset.util;

import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;

/**
 * A utility do-little implementation
 * of the FilesetErrorHandler interface
 * @author Markus Gylling
 */
public class DefaultFilesetErrorHandlerImpl implements FilesetErrorHandler {
	private boolean throwOnFatal = false;
	
	public DefaultFilesetErrorHandlerImpl(){
		
	}

	public DefaultFilesetErrorHandlerImpl(boolean throwOnFatalFileException){
		throwOnFatal = throwOnFatalFileException;
	}

	public void error(FilesetFileException ffe) throws FilesetFileException {
		if(ffe instanceof FilesetFileFatalErrorException) {
			System.err.println("Serious error in " 
					+ ffe.getOrigin().getName() + ": " + ffe.getCause().getMessage() + " [" + ffe.getCause().getClass().getSimpleName() + "]");
			if(throwOnFatal) throw ffe;
		}else if (ffe instanceof FilesetFileErrorException) {
			System.err.println("Error in " 
					+ ffe.getOrigin().getName() + ": " + ffe.getCause().getMessage() + " [" + ffe.getCause().getClass().getSimpleName() + "]");
		}else if (ffe instanceof FilesetFileWarningException) {
			System.err.println("Warning in " 
					+ ffe.getOrigin().getName() + ": " + ffe.getCause().getMessage() + " [" + ffe.getCause().getClass().getSimpleName() + "]");
		}else{
			System.err.println("Exception with unknown severity in " 
					+ ffe.getOrigin().getName() + ": " + ffe.getCause().getMessage() + " [" + ffe.getCause().getClass().getSimpleName() + "]");
		}
	}

}
