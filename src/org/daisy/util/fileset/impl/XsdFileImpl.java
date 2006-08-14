package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.SchemaFile;
import org.daisy.util.fileset.interfaces.schema.XsdFile;
import org.xml.sax.SAXException;

final class XsdFileImpl extends XmlFileImpl implements XsdFile, SchemaFile {
			
	XsdFileImpl(URI uri) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		super(uri, XsdFile.mimeStringConstant);
	}
	
	private static final long serialVersionUID = 4187774370911485353L;

}
