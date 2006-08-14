package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.SchemaFile;
import org.daisy.util.fileset.interfaces.schema.RelaxngFile;
import org.xml.sax.SAXException;

final class RelaxngFileImpl extends XmlFileImpl implements RelaxngFile, SchemaFile {

	RelaxngFileImpl(URI uri) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		super(uri, RelaxngFile.mimeStringConstant);
	}
	
	private static final long serialVersionUID = 6602854148244873869L;		

}
