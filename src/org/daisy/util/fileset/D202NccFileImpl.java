package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * Represents the Navigation Control Center (NCC) file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */
class D202NccFileImpl extends Xhtml10FileImpl implements D202NccFile {
	
    D202NccFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
        super(uri);          
    }
    
    D202NccFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException {
        super(uri, errh);          
    }
        
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i).trim().intern();
			attrValue = attrs.getValue(i).trim().intern();							
			if (attrName=="id") {
				putIdValue(attrValue);
			}else if (regex.matches(regex.XHTML_ATTRS_WITH_URIS,attrName)) {
				putUriValue(attrValue);
			}					
		} //for (int i
	}			
}
