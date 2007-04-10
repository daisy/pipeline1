package org.daisy.util.xml.validation.jaxp;

import javax.xml.transform.Source;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Capture an incoming SRVL SAX Stream and redirect to 
 * javax.xml.validation.Validator ErrorHandler output. 
 * @author Markus Gylling
 */
public class ISOSchematronSVRLHandler extends DefaultHandler {
	private ErrorHandler mErrorHandler = null;
	private String mCurrentSystemId = null;
	private StringBuilder message = null;
	private boolean inText = false;
	private boolean inFailedAssert = false;

	public ISOSchematronSVRLHandler(ErrorHandler errh, Source source) {
		mErrorHandler = errh;
		mCurrentSystemId = source.getSystemId();
		message = new StringBuilder();
	}

	public void setDocumentLocator(Locator locator) {
		if(locator.getSystemId()!=null) mCurrentSystemId = locator.getSystemId();		
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {		
		if(inText && inFailedAssert){
			mErrorHandler.error(new SAXParseException(message.toString().trim(), null, mCurrentSystemId,-1,-1));
			message.delete(0, message.length());
		}
		if(localName.equals("text")) inText = false;
		if(localName.equals("failed-assert")) inFailedAssert = false;

	}
	
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals("text")) inText = true;
		if(localName.equals("failed-assert")) inFailedAssert = true;
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		if(inText && inFailedAssert){
			for (int i = start; i < length; i++) {
				message.append(ch[i]);
			}
		}
	}
}
