package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class XslFileImpl extends XmlFileImpl implements XslFile {

	XslFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri);
	}	

	XslFileImpl(URI uri, ErrorHandler errh) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri, errh);
	}	

	//TODO override XMLFile startelement here and get whatever resources xsl may reference
}
