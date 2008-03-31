package org.daisy.util.fileset.util;

import java.io.IOException;
import java.util.Collection;

import org.daisy.util.file.EFolder;

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
	public static Collection getManifests(boolean deep, EFolder baseFolder) throws IOException {
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
