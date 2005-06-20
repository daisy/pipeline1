/*
 * Created on 2005-jun-19
 */
package org.daisy.util.exception;

import org.xml.sax.SAXException;

/**
 * @author Markus Gylling
 */
public class SAXStopParsingException extends SAXException {

	public SAXStopParsingException() {
		super();
	}

	public SAXStopParsingException(String message) {
		super(message);
	}

	public SAXStopParsingException(Exception e) {
		super(e);
	}

	public SAXStopParsingException(String message, Exception e) {
		super(message, e);
	}

}
