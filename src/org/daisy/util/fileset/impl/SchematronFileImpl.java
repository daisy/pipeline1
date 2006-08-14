package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.SchemaFile;
import org.daisy.util.fileset.interfaces.schema.SchematronFile;
import org.xml.sax.SAXException;

final class SchematronFileImpl extends XmlFileImpl implements SchematronFile, SchemaFile {

	SchematronFileImpl(URI uri) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		super(uri, SchematronFile.mimeStringConstant);
	}

	
	private static final long serialVersionUID = -8871123377479495660L;	

}
