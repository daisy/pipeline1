package org.daisy.util.fileset.impl;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.z3986.Z3986SmilFile;
import org.daisy.util.mime.MIMETypeException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class Z3986SmilFileImpl extends SmilFileImpl implements Z3986SmilFile {

	public Z3986SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri,Z3986SmilFile.mimeStringConstant);	
	}
	
	public Z3986SmilFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri, errh,Z3986SmilFile.mimeStringConstant);	
	}
	
}
