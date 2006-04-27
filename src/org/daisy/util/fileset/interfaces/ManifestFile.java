package org.daisy.util.fileset.interfaces;

import java.io.File;



/**
 * interface for the manifest file types (opf, ncc, playlist, etc)
 * @author Markus Gylling
 */
public interface ManifestFile extends FilesetFile, Referring {

	public File getFile();
	
}
