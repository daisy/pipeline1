/*
 * Created on 2005-jun-17
 */
package org.daisy.util.fileset;

import java.io.IOException;
import javazoom.jl.decoder.BitstreamException;
import org.xml.sax.SAXException;

/**
 * <p>Base representation of the {@link org.daisy.util.fileset} member hierarchy.</p>
 * @author Markus Gylling
 */
public interface FilesetFile extends JavaFile, Referable {
	
	/**
	 * <p>A generic command to make the member populate its own properties; at the very least:</p>
	 * <ul>
	 * <li>URIs (if existing) to uriStrings</li>
	 * <li>IDs (if existing) to idStrings</li>
	 * </ul>
	 */
	
	public abstract void parse()throws IOException, SAXException, BitstreamException;

	/**
	 * <p>Returns the mime-type of this filetype.</p>
	 * <p>Some Mimetypes are IANA classics;
	 * other are Z3986 defined, others are
	 * ad-hoc strings.</p>
	 * <p>All mimetypes returned by this method are retrieved from
	 * {@link FilesetConstants}.
	 * @see FilesetConstants
	 */
	public abstract String getMimeType();			
	
}
