/*
 * Created on 2005-jun-17
 */
package org.daisy.util.fileset;

import java.io.IOException;
import java.util.HashMap;

import javazoom.jl.decoder.BitstreamException;
import org.xml.sax.SAXException;

/**
 * <p>Base representation of the {@link org.daisy.util.fileset} member hierarchy.</p>
 * @author Markus Gylling
 */
public interface FilesetFile extends JavaFile, Referable {
	
	/**
	 * a generic command to make the member populate its own properties; at the very least
	 * URIs (if existing) to uriStrings
	 * IDs (if existing) to idStrings
	 */
	
	public abstract void parse()throws IOException, SAXException, BitstreamException;
	
}
