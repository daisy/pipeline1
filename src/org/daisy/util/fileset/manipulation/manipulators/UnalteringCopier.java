/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.fileset.manipulation.manipulators;

import java.io.File;
import java.io.IOException;

import org.daisy.util.file.FileUtils;
import org.daisy.util.fileset.FilesetFile;
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
