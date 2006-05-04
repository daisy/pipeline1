package org.daisy.util.fileset.impl;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.d202.D202SmilFile;
import org.daisy.util.xml.SmilClock;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
  */
final class D202SmilFileImpl extends SmilFileImpl implements D202SmilFile {

	public D202SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri, D202SmilFile.mimeStringConstant);		
	}
			
	public InputSource resolveEntity(String publicId, String systemId) throws IOException {
		//override the XmlFileImpl method in order to substitute DTDs
		//from w3c smil to the subset one					
		publicId = "-//DAISY//DTD smil v2.02//EN";			
		return super.resolveEntity(publicId,systemId);
	}
	
	public SmilClock getStatedTotalElapsedTime() {
		return myStatedTotalElapsedTime;
	}
	
	private static final long serialVersionUID = -4124499324926116684L;
}