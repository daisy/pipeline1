package org.daisy.util.fileset.manipulation;

import org.daisy.util.fileset.interfaces.FilesetErrorHandler;
import org.daisy.util.fileset.interfaces.FilesetFile;

public interface FilesetManipulatorListener extends FilesetErrorHandler{
	
	/**
	 * Single method interface, exposing each FilesetFile to the listener; the listener 
	 * decides what action to perform by returning an implementation
	 * of FilesetFileManipulator. nextFile method invocations are pushed 
	 * from the FilesetManipulator, not pulled.
	 * @param file Fileset member
	 * @return an implementation of FilesetFileManipulator, or
	 * null, in which case the typical consequence is that inparam 
	 * file is copied unaltered to the destination.
	 */
	public FilesetFileManipulator nextFile(FilesetFile file) throws FilesetManipulationException;
	
}
