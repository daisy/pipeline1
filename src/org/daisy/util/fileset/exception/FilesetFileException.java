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

package org.daisy.util.fileset.exception;

import org.daisy.util.exception.BaseException;
import org.daisy.util.fileset.FilesetFile;

/**
 * <p>A wrapper for any exception reported through the FilesetErrorHandler interface</p>
 * <p>This class is extended with subclasses declaring error severity.</p>
 * @see FilesetFileErrorException
 * @see FilesetFileFatalErrorException 
 * @see FilesetFileWarningException
 * @author Markus Gylling 
 */

public class FilesetFileException extends BaseException {
	private FilesetFile owner;
	private Throwable outercause;
	
	public FilesetFileException(FilesetFile origin, Throwable exc) {
		super("Fileset exception:", exc);
		this.outercause = exc;
		this.owner = origin;
	}

	/**
	 * @return the actual exception that occured
	 */
	public Throwable getCause() {
		return this.outercause;
	}
	
	/**
	 * @return the FilesetFile instance in which the exception occured
	 */
	public FilesetFile getOrigin(){
		return this.owner;
	}
	
	private static final long serialVersionUID = -2070247113922085583L;
	
}
