package org.daisy.util.fileset.impl;

import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.exception.FilesetFileErrorException;
import org.daisy.util.fileset.interfaces.ManifestFile;
import org.daisy.util.fileset.interfaces.xml.TextualContentFile;
import org.daisy.util.fileset.interfaces.xml.Xhtml10File;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */

class Xhtml10FileImpl extends XmlFileImpl implements TextualContentFile, Xhtml10File, ManifestFile {
	private int currentHeadingLevel =0;
	private boolean correctHeadingSequence = true;
	protected boolean parsingBody = false;
	
	Xhtml10FileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException {
		super(uri,Xhtml10File.mimeStringConstant); 
	}	
	
	Xhtml10FileImpl(URI uri, String mimeStringConstant) throws ParserConfigurationException, SAXException, IOException {
		super(uri,mimeStringConstant); 
	}	
						
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {
		if(sName=="body") parsingBody = true;
		for (int i = 0; i < attrs.getLength(); i++) {
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern(); //for some reason	
						
			if (attrName=="id") {
				QName q = new QName(namespaceURI,sName);
				this.putIdAndQName(attrValue,q);				
			}else if(regex.matches(regex.XHTML_ATTRS_WITH_URIS,attrName)) {
				putUriValue(attrValue);
			}else if (attrName=="xml:lang") {
				this.xmlLangValues.add(attrValue);
			}						
		} //for (int i
	}
	
	public boolean hasHierarchicalHeadingSequence() {
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
					myExceptions.add(new FilesetFileErrorException(this,e));
				}				
			}
		}
	}
	
	private static final long serialVersionUID = 699491683089715725L;
}
