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
package org.daisy.util.fileset.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.daisy.util.file.Directory;

/**
 * Given an input filesystem directory, locates all 
 * {@link org.daisy.util.fileset.ManifestFile} 
 * files within it and its subfolders.
 * @author Markus Gylling
 */
public class ManifestFinder {
	
	/**
	 * @return a Collection of File containing all files within set basefolder whose filename
	 * patterns indicate that they implement the 
	 * {@link org.daisy.util.fileset.ManifestFile} interface.
	 * @param deep whether to recurse subfolders.
	 * @param baseFolder filessystem directory to search within
	 * @throws IOException 
	 */
	public static Collection<File> getManifests(boolean deep, Directory baseFolder) throws IOException {
		if(!baseFolder.exists()) throw new IOException(baseFolder.getPath() + " does not exist");
		
		FilesetRegex rgx = FilesetRegex.getInstance();
		StringBuilder sb = new StringBuilder();
		sb.append('(');		
		sb.append(rgx.FILE_CSS.toString());
		sb.append(")|(");
		sb.append(rgx.FILE_DTBOOK.toString());
		sb.append(")|(");
		sb.append(rgx.FILE_M3U.toString());
		sb.append(")|(");
		sb.append(rgx.FILE_NCC.toString());
		sb.append(")|(");
		sb.append(rgx.FILE_OPF.toString());
		sb.append(")|(");
		sb.append(rgx.FILE_PLS.toString());
		sb.append(")|(");
		sb.append(rgx.FILE_RESOURCE.toString());
		sb.append(")|(");
		sb.append(rgx.FILE_NCC.toString());
		sb.append(")|(");
		sb.append(rgx.FILE_XHTML.toString());
		sb.append(')');
		
		return baseFolder.getFiles(deep, sb.toString(), false);
		
		//TODO could peek on the result of .getFiles to filter nonmanifests (eg *.xml) out.
	}
	
}
