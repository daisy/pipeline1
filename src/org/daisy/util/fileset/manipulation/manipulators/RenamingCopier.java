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
