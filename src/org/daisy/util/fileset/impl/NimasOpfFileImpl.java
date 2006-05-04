package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.z3986.NimasOpfFile;
import org.xml.sax.SAXException;

final class NimasOpfFileImpl extends OpfFileImpl implements NimasOpfFile{
	
	NimasOpfFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri, NimasOpfFile.mimeStringConstant);
	}

	private static final long serialVersionUID = -2525728609337273429L;
}