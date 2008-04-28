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
package org.daisy.util.fileset;

import org.daisy.util.fileset.exception.FilesetFileException;

/**
 * @author Markus Gylling
 */
public interface FilesetErrorHandler {

	/**
	 * <p>This interface defines a singular method through
	 * which notification of the following types of Exceptions 
	 * can be received.</p>
	 * <ul>
	 * <li>FilesetFileFatalErrorException -  extends FilesetFileException - severity fatal for this file (example XML malformedness)</li>
	 * <li>FilesetFileErrorException -  extends FilesetFileException - severity nonfatal but critical for this file (example XML invalidity when validation is on)</li>
	 * <li>FilesetFileWarningException -  extends FilesetFileException - low severity - file still usable without needing to expect critical data access failures</li>
	 * <li>FilesetFileException - super type - severity unspecified</li> 
	 * </ul>
	 * <p>A default implementation of this interface exists in
	 * org.daisy.util.fileset.util.DefaultFilesetErrorHandlerImpl</p>
	 */
	
	public void error(FilesetFileException ffe) throws FilesetFileException;
		
}
