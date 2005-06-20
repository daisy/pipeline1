package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
class D202MasterSmilFileImpl extends SmilFileImpl implements D202MasterSmilFile {
	private boolean inBody = false;
	
	public D202MasterSmilFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri);
	}
	
	public void startElement (String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {        		
		if(inBody){
			for (int i = 0; i < attrs.getLength(); i++) {			
				attrName = attrs.getQName(i).trim().intern();
				attrValue = attrs.getValue(i).trim().intern();					
				if (attrName=="id") {
					this.putIdValue(attrValue);
				}else if (attrName=="src") {
					this.putUriValue(attrValue);
				}	
			}//for int i
		}
	}//startElement
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(qName.equals("head")) inBody = true;
	}
}
