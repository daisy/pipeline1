package org.daisy.util.fileset.util;

import org.daisy.util.fileset.FilesetFile;

/**
 * A filter for FilesetFiles.
 * @author Markus Gylling
 */
public interface FilesetFileFilter {
	
	public static final short REJECT = 0;
	public static final short ACCEPT = 1;
	
	public short acceptFile(FilesetFile file);
	
}
