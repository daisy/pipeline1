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

package org.daisy.util.fileset.util;

import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.exception.FilesetFileException;
import org.daisy.util.fileset.exception.FilesetFileFatalErrorException;
import org.daisy.util.fileset.exception.FilesetFileWarningException;
import org.daisy.util.fileset.interfaces.FilesetErrorHandler;

/**
 * A utility do-little implementation
 * of the FilesetErrorHandler interface
 * @author Markus Gylling
 */
public class DefaultFilesetErrorHandlerImpl implements FilesetErrorHandler {
	private boolean throwOnFatal = false;
	private static StringBuilder sb = null;
	
	public DefaultFilesetErrorHandlerImpl(){
		
	}

	public DefaultFilesetErrorHandlerImpl(boolean throwOnFatalFileException){
		throwOnFatal = throwOnFatalFileException;
	}

	public void error(FilesetFileException ffe) throws FilesetFileException {
		if(null==sb)sb = new StringBuilder();
		sb.delete(0,sb.length());
		sb.append(ffe.getOrigin().getName());
		sb.append(' ');
		sb.append(':');
		sb.append(ffe.getCause().getMessage());
		sb.append(' ');
		sb.append('[');
		sb.append(ffe.getCause().getClass().getSimpleName());
		sb.append(']');
		
		if(ffe instanceof FilesetFileFatalErrorException) {
			sb.insert(0,"Serious error in ");
			System.err.println(sb.toString());
			if(throwOnFatal) throw ffe;
		}else if (ffe instanceof FilesetFileErrorException) {
			sb.insert(0,"Error in ");
			System.err.println(sb.toString());
		}else if (ffe instanceof FilesetFileWarningException) {
			sb.insert(0,"Warning in ");
			System.err.println(sb.toString());
		}else{
			sb.insert(0,"Exception with unknown severity in ");
			System.err.println(sb.toString());
		}
	}

}