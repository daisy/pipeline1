package org.daisy.util.fileset.util;

import java.io.IOException;
import java.util.Collection;

import org.daisy.util.file.EFolder;

/**
 * Given an input filesystem directory, locates all 
 * {@link org.daisy.util.fileset.interfaces.ManifestFile} 
 * files within it and its subfolders.
 * @author Markus Gylling
 */
public class ManifestFinder {
	private EFolder mBaseFolder = null;	  				 //folder to start recursing from
			 	
	public ManifestFinder(EFolder baseFolder) throws IOException {	
		if(!baseFolder.exists()) throw new IOException();
		this.mBaseFolder = baseFolder;
	}
	
	/**
	 * @return a Collection of File containing all files within set basefolder whose filename
	 * patterns indicate that they implement the 
	 * {@link org.daisy.util.fileset.interfaces.ManifestFile} interface.
	 * @param deep whether to recurse subfolders.
	 */
	public Collection getManifests(boolean deep) {
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
		sb.append(rgx.FILE_XHTML.toString());
		sb.append(')');
		
		return mBaseFolder.getFiles(deep, sb.toString(), false);
	}
	
}
