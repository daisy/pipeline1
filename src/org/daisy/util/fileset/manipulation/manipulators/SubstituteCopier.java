package org.daisy.util.fileset.manipulation.manipulators;

import java.io.File;
import java.io.IOException;

import org.daisy.util.fileset.interfaces.FilesetFile;
import org.daisy.util.fileset.manipulation.FilesetFileManipulator;
import org.daisy.util.fileset.manipulation.FilesetManipulationException;

/**
 * Performs input substitution using a replacement physical file.
 * @author Markus Gylling
 */
public class SubstituteCopier extends UnalteringCopier implements FilesetFileManipulator {
	private FilesetFile substitute = null;

	/**
	 * Substitutes the source (input fileset) file with an entirely other file.
	 * Will attempt to use the name of source (input fileset) file for the substitute filename.
	 * @param substitute
	 */
	public SubstituteCopier(FilesetFile substitute) {
		super();
		this.substitute  = substitute;		
	}
	
	public File manipulate(FilesetFile inFile, File destination, boolean allowDestinationOverwrite) throws FilesetManipulationException {
		
		try {
			//attempt the rename of the substitute into the source files name
			substitute.getFile().renameTo(new File(substitute.getParentFolder(),inFile.getName()));
		} catch (IOException e) {
		
		}
		return super.manipulate(substitute,destination,allowDestinationOverwrite);
	}
}
