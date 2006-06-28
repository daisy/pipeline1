package org.daisy.util.fileset.manipulation.manipulators;

import java.io.File;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;

public class RenamingCopier extends UnalteringCopier implements FilesetFileManipulator {
	private String newName = null;
	
	public RenamingCopier(String newFileLocalName) {
		super();
		this.newName = newFileLocalName;		
	}

	/**
	 * The destination inparam does not (necessarily) have the newName set.
	 */
	public File manipulate(FilesetFile inFile, File destination, boolean allowDestinationOverwrite) throws FilesetManipulationException {
		//manipulate the destination
		File newDestination = new File(destination.getParentFile(), newName);
		//call super
		return super.manipulate(inFile,newDestination,allowDestinationOverwrite);
	}
	
}
