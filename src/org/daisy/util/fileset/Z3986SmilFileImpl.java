package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class Z3986SmilFileImpl extends SmilFileImpl implements Z3986SmilFile {

	/**
	 * @param uri
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Z3986SmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri);	
		parse();
	}
}
