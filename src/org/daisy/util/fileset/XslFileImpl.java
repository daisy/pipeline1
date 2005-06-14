package org.daisy.util.fileset;

import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
public class XslFileImpl extends XmlFileImpl implements XmlFile {

	XslFileImpl(URI uri) throws ParserConfigurationException, SAXException {
		super(uri);
	}	
	//TODO override XMLFile startelement here and get whatever resources xsl may reference
}
