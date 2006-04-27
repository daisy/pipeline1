package org.daisy.util.fileset.impl;

import java.io.IOException;
import java.net.URI;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.util.fileset.interfaces.xml.d202.D202NccFile;
import org.daisy.util.mime.MIMEConstants;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.xml.SmilClock;
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
	private String myDcIdentifier = null;
	private String myDcTitle = null;
	
	private boolean hasNccSetInfo = false;
	private boolean hasRelAttrsInBody = false;
		
    D202NccFileImpl(URI uri) throws ParserConfigurationException, SAXException, IOException, MIMETypeException {
        super(uri, D202NccFile.mimeStringConstant);          
    }
    
    D202NccFileImpl(URI uri, ErrorHandler errh) throws ParserConfigurationException, SAXException, IOException, MIMETypeException {
        super(uri, errh, D202NccFile.mimeStringConstant);          
    }
    
	public void startElement(String namespaceURI, String sName, String qName, Attributes attrs) throws SAXException {		
		for (int i = 0; i < attrs.getLength(); i++) {					
			attrName = attrs.getQName(i);
			attrValue = attrs.getValue(i).intern(); //for some reason							
			if (attrName=="id") {
				putIdAndQName(attrValue,qName);
			}else if (regex.matches(regex.XHTML_ATTRS_WITH_URIS,attrName)) {
				putUriValue(attrValue);
			}
			
			if (this.parsingBody && attrName=="rel") {
				this.hasRelAttrsInBody = true;
			}
			
			try {
				if (sName=="meta") {
					if (attrValue=="ncc:totalTime") {
						myStatedDuration = new SmilClock(attrs.getValue("content")); 
					}else if (attrValue=="dc:identifier") {
						myDcIdentifier = attrs.getValue("content");
					}else if (attrValue=="dc:title") {
						myDcTitle = attrs.getValue("content");
					}else if (attrValue=="ncc:setInfo"||attrValue=="ncc:setinfo") {
						String setinfovalue = attrs.getValue("content");
						if (!setinfovalue.equals("1 of 1")){
							this.hasNccSetInfo = true;
						}
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
	
	public String getDcIdentifier() {
		return myDcIdentifier;
	}
	
	public String getDcTitle() {
		return myDcTitle;
	}

	public boolean hasMultiVolumeIndicators() {
		if(this.hasRelAttrsInBody && this.hasNccSetInfo) {
			return true;
		}
		return false;
	}

	private static final long serialVersionUID = 1009746859210460470L;
}
