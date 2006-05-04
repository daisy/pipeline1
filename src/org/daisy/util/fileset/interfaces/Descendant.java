package org.daisy.util.fileset.interfaces;

import java.io.File;

/**
 * Any descendant of java.io.File 
 * implements this interface.
 * @author Markus Gylling
 */

public interface Descendant {

	/**
	 * @return the underlying java.io.File instance
	 */
	public File getFile();
	
}
