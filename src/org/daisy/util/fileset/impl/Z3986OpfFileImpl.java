package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.z3986.Z3986OpfFile;
import org.daisy.util.xml.SmilClock;
import org.xml.sax.SAXException;

final class Z3986OpfFileImpl extends OpfFileImpl implements Z3986OpfFile{
	
	Z3986OpfFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri, Z3986OpfFile.mimeStringConstant);
	}

	public SmilClock getStatedDuration() {		
		return statedDuration;
	}

	public String getMetaDtbMultiMediaType() {
		return statedMultiMediaType;
	}
	
	private static final long serialVersionUID = 7449994487926699133L;
}