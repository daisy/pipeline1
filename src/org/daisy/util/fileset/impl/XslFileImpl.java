package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.XslFile;
import org.daisy.util.mime.MIMETypeException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class XslFileImpl extends XmlFileImpl implements XslFile {

	XslFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri,XslFile.mimeStringConstant);
	}	

	XslFileImpl(URI uri, ErrorHandler errh) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri, errh,XslFile.mimeStringConstant);
	}	

	private static final long serialVersionUID = 1529703643419882930L;
	
}
