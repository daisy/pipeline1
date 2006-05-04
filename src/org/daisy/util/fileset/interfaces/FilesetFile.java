package org.daisy.util.fileset.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;

import javazoom.jl.decoder.BitstreamException;

import org.daisy.util.file.IEFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>Base representation of the {@link org.daisy.util.fileset} member hierarchy.</p>
 * @author Markus Gylling
 */

public interface FilesetFile extends Referable, IEFile {
	
	/**
	 * <p>A generic command to make the member populate its own properties; often including:</p>
	 * <ul>
	 * <li>URIs (if existing)</li>
	 * <li>ID/QName pairs (if existing)</li>
	 * <li>duration (if existing)</li>
	 * <li>metadata</li>
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
	 *@return true if this FilesetFile has been parsed	 
	 */
	public boolean isParsed();	

	/**
	 *@return true if errors (exceptions) where reported 
	 *during instantiation of this FilesetFile.
	 *@see #getErrors()	 
	 */
	public boolean hadErrors();	

	/**
	 *@return a collection&lt;Exception&gt; reported 
	 *during instantiation of this FilesetFile.
	 *@see #hadErrors()	 
	 */
	public Collection getErrors();
	
	/**
	 **@return this file as an {@link org.xml.sax.InputSource}
	 */
	public InputSource asInputSource() throws FileNotFoundException;	
	
	/**
	 **@return this file as a FileInputStream
	 */
	public InputStream asInputStream() throws FileNotFoundException;

}
