/*
 * Created on 2005-jun-17
 */
package org.daisy.util.fileset;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Abstract interface to bring some methods along from {@link java.io.File} 
 * into {@link org.daisy.util.fileset.FilesetFile} 
 * @author Markus Gylling 
 */
public abstract interface JavaFile {
	boolean canRead();
	boolean canWrite();
	String getName();
	String getParent();
	File getParentFile();
	File getFile();
	URI toURI();
	URL toURL() throws MalformedURLException;
}
