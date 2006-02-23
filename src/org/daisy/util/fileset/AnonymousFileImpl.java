/*
 * Created on 2005-jun-20
 */
package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import javazoom.jl.decoder.BitstreamException;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class AnonymousFileImpl extends FilesetFileImpl {
  
	public AnonymousFileImpl(URI uri) throws FileNotFoundException, IOException {
		super(uri);		
	}

	public void parse() throws IOException, SAXException, BitstreamException {}

	public String getMimeType() {
		return "application/x-anonymous";
	}	
}
