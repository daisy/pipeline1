package org.daisy.util.fileset.impl;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile;
import org.daisy.util.mime.MIMETypeException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
  */
class D202SmilFileImpl extends SmilFileImpl implements D202SmilFile {

	public D202SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri, D202SmilFile.mimeStringConstant);		
	}
	
	public D202SmilFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException, MIMETypeException {
		super(uri, errh,D202SmilFile.mimeStringConstant);		
	}
		
	private static final long serialVersionUID = -4124499324926116684L;
}
