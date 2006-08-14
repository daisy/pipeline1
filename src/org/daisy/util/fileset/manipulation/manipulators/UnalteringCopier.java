package org.daisy.util.fileset.manipulation.manipulators;

import java.io.File;
import java.io.IOException;

import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;

/**
 * Copies a Fileset file unaltered to the destination
 * @param overwrite if true, overwrite an existing destination, 
 * if false, throw an exception if destination exists
 * @author Markus Gylling
 */

public class UnalteringCopier implements FilesetFileManipulator {
	 
	/** 
	 * Default constructor
	 */
	public UnalteringCopier(){
		
	}

	public File manipulate(FilesetFile inFile, File destination, boolean allowDestinationOverwrite) throws FilesetManipulationException {

		if(destination.exists() && !allowDestinationOverwrite) {
			throw new FilesetManipulationException(destination.getName() + " exists");
		}
		
		try {
			FileUtils.copyFile((File)inFile, destination);
		} catch (IOException e) {
			throw new FilesetManipulationException(e.getMessage(),e);
		}		
		return destination;
	}
}
