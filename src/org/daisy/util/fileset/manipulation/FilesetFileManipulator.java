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
