package org.daisy.util.fileset.manipulation;

import java.io.File;

import org.daisy.util.fileset.interfaces.FilesetFile;

/**
 * Interface that any manipulator of a FilesetFile must implement in
 * the FilesetManipulator context. The implementation constructor is
 * used to to provide any needed context information.
 * @author Markus Gylling
 */
public interface FilesetFileManipulator {
	/**
	 * Perform a manipulation,
	 * and return a pointer to the result File 
	 */
	public File manipulate(FilesetFile inFile, File destination, boolean allowDestinationOverwrite) throws FilesetManipulationException;
	
}
