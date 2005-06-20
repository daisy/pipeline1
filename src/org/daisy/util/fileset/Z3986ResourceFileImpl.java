package org.daisy.util.fileset;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class Z3986ResourceFileImpl extends XmlFileImpl implements Z3986ResourceFile {

	Z3986ResourceFileImpl(URI uri) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri);		
	}
	
	Z3986ResourceFileImpl(URI uri, ErrorHandler errh) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		super(uri, errh);		
	}
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		qName = qName.intern();
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i).trim().intern();
			attrValue = attrs.getValue(i).trim().intern();
			
			if (attrName=="id") {
				putIdValue(attrValue);
			}else if (attrName=="src") {
				putUriValue(attrValue);
			}					
		} //for (int i							
	}
}
