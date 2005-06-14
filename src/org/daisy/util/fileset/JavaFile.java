
package org.daisy.util.fileset;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author Markus Gylling 
 */
abstract interface JavaFile {
	boolean canRead();
	boolean canWrite();
	String getName();
	String getParent();
	File getParentFile();
	URI toURI();
	URL toURL() throws MalformedURLException;
}
