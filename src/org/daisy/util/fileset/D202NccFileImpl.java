package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Represents the Navigation Control Center (NCC) file in a Daisy 2.02 fileset
 * @author Markus Gylling
 */
class D202NccFileImpl extends Xhtml10FileImpl implements D202NccFile {
	private SmilClock myStatedDuration = null;
	
    D202NccFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
        super(uri);          
    }
    
    D202NccFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException {
        super(uri, errh);          
    }
        
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		for (int i = 0; i < attrs.getLength(); i++) {
//			attrName = attrs.getQName(i).intern();
//			attrValue = attrs.getValue(i).intern();							
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern(); //for some reason							
			if (attrName=="id") {
				putIdAndQName(attrValue,qName);
			}else if (regex.matches(regex.XHTML_ATTRS_WITH_URIS,attrName)) {
				putUriValue(attrValue);
			}
			
			try {
				if (sName=="meta") {
					if (attrValue=="ncc:totalTime") {
						myStatedDuration = new SmilClock(attrs.getValue("content"));
					}
				}				
			} catch (Exception nfe) {
				this.listeningErrorHandler.error(new SAXParseException(this.getName()+": exception when calculating " +attrValue,null));
				
			}
			
		} //for (int i
	}	
	
	public SmilClock getStatedDuration() {				
		return myStatedDuration;
	}
}
