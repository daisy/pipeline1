package org.daisy.util.fileset.interfaces;

import org.daisy.util.fileset.exception.FilesetFileException;

/**
 * @author Markus Gylling
 */
public interface FilesetErrorHandler {

	/**
	 * <p>This interface defines a singular method through
	 * which notification of the following types of Exceptions 
	 * can be recieved.</p>
	 * <ul>
	 * <li>FilesetFileFatalErrorException -  extends FilesetFileException - severity fatal for this file (example XML malformedness)</li>
	 * <li>FilesetFileErrorException -  extends FilesetFileException - severity nonfatal but critical for this file (example XML invalidity when validation is on)</li>
	 * <li>FilesetFileWarningException -  extends FilesetFileException - low severity - file still usable without needing to expect critical data access failures</li>
	 * <li>FilesetFileException - super type - severity unspecified</li> 
	 * </ul>
	 * <p>A default implementation of this interface exists in
	 * org.daisy.util.fileset.util.DefaultFilesetErrorHandlerImpl</p>
	 */
	
	public void error(FilesetFileException ffe) throws FilesetFileException;
		
}
