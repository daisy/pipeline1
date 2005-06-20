package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class Z3986SmilFileImpl extends SmilFileImpl implements Z3986SmilFile {

	public Z3986SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri);	
	}
	
	public Z3986SmilFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException {
		super(uri, errh);	
	}
}
