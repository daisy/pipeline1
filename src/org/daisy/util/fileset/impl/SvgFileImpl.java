package org.daisy.util.fileset.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.SvgFile;
import org.xml.sax.SAXException;

/**
 * @author jpritchett
 *
 * File implementation for SVG image files
 */
public class SvgFileImpl extends XmlFileImpl implements SvgFile {
	public SvgFileImpl(URI uri) throws FileNotFoundException, IOException, SAXException, ParserConfigurationException {
		super(uri, SvgFile.mimeStringConstant);
	}

}
