package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.XslFile;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class XslFileImpl extends XmlFileImpl implements XslFile {

	XslFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri,XslFile.mimeStringConstant);
	}	

	private static final long serialVersionUID = 1529703643419882930L;
	
}