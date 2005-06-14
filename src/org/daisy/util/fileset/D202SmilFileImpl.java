package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
  */
class D202SmilFileImpl extends SmilFileImpl implements D202SmilFile {

	/**
	 * @param uri
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public D202SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri);
		parse();
	}
}
