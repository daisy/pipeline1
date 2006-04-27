package org.daisy.util.fileset.interfaces;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javazoom.jl.decoder.BitstreamException;

import org.daisy.util.file.EFile;
import org.xml.sax.SAXException;

/**
 * <p>Base representation of the {@link org.daisy.util.fileset} member hierarchy.</p>
 * @author Markus Gylling
 */

public interface FilesetFile extends Referable, EFile {
	
	/**
	 * <p>A generic command to make the member populate its own properties; at the very least:</p>
	 * <ul>
	 * <li>URIs (if existing)</li>
	 * <li>IDs (if existing)</li>
	 * </ul>
	 */	
	public abstract void parse() throws IOException, SAXException, BitstreamException;

	/**
	 * Gets a relative URI of this file relative to the inparam file
	 * (=a relative URI where this file points to the inparam file)
	 * @param filesetFile a member of the fileset
	 * @return a relative URI
	 */
	public URI getRelativeURI(FilesetFile filesetFile);
	
	/**
	 * Gives access to methods of java.io.File
	 */
	public File getFile();
}
