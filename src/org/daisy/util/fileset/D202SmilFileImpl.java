package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
  */
class D202SmilFileImpl extends SmilFileImpl implements D202SmilFile {

	public D202SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri);		
	}
	
	public D202SmilFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException {
		super(uri, errh);		
	}
}
