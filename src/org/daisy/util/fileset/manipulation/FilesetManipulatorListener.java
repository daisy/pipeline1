/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.util.fileset.manipulation;

import org.daisy.util.fileset.FilesetErrorHandler;
import org.daisy.util.fileset.FilesetFile;

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
