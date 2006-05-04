package org.daisy.util.fileset.impl;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.z3986.Z3986SmilFile;
import org.daisy.util.xml.SmilClock;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
final class Z3986SmilFileImpl extends SmilFileImpl implements Z3986SmilFile {

	public Z3986SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri,Z3986SmilFile.mimeStringConstant);	
	}
		
	public SmilClock getStatedTotalElapsedTime() {
		return myStatedTotalElapsedTime;
	}
	
	private static final long serialVersionUID = 1360195717746938439L;
}
