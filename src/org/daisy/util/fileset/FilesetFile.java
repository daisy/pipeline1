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
package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collection;

import javazoom.jl.decoder.BitstreamException;

import org.daisy.util.file.IEFile;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>Base representation of the <code>org.daisy.util.fileset</code> member hierarchy.</p>
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
	 * Gets an absolute URI from a relative URI within this file
	 * @param relativeURI String of unresolved URI within this file
	 * @return an absolute URI
	 */
	public URI getAbsolutizedURI(String relativeURI);
	
	
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
	public Collection<Exception> getErrors();
	
	/**
	 **@return this file as an {@link org.xml.sax.InputSource}
	 */
	public InputSource asInputSource() throws FileNotFoundException;	
	
	/**
	 **@return this file as a FileInputStream
	 */
	public InputStream asInputStream() throws FileNotFoundException;

	/**
	 * @return this file as a byte array 
	 * where each byte is represented as a decimal int (0-255)
	 */
	public byte[] asByteArray() throws IOException;
	
	/**
	 * @return this file as a ByteBuffer 
	 */
	public ByteBuffer asByteBuffer() throws IOException;
			
}
