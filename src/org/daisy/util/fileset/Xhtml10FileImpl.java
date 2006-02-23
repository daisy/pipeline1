package org.daisy.util.fileset;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */

class Xhtml10FileImpl extends XmlFileImpl implements TextualContentFile, Xhtml10File, ManifestFile {
		
	private int currentHeadingLevel =0;
	private boolean correctHeadingSequence = true;
	protected boolean parsingBody = false;
	
	Xhtml10FileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri); 
	}	
	
	Xhtml10FileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException {
		super(uri, errh);          
	}
	
	public String getMimeType() {
	      return FilesetConstants.MIMETYPE_XHTML10;
	}
	
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		if(sName=="body") parsingBody = true;
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern(); //for some reason	
			if (attrName=="id") {
				putIdAndQName(attrValue,qName);
			}else if(regex.matches(regex.XHTML_ATTRS_WITH_URIS,attrName)) {
				putUriValue(attrValue);
			}						
		} //for (int i
	}
	
	public boolean hasCorrectHeadingSequence() {
		return correctHeadingSequence;
	}
	
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName=="body") parsingBody = false;
		if (correctHeadingSequence == true){
			if(regex.matches(regex.XHTML_HEADING_ELEMENT,localName)) {
				try{					
					int newHeadingLevel = Integer.parseInt(localName.substring(1,2));						
					if(newHeadingLevel-1 > currentHeadingLevel){
						correctHeadingSequence = false;	
					}
					currentHeadingLevel = newHeadingLevel;
				}catch (Exception e) {
					System.err.println("exception in Xhtml10FileImpl endelement");
				}
				
			}
		}
	}
}
