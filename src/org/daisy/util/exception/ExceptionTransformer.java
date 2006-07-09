package org.daisy.util.exception;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;
/**
 * 
 * @author Markus Gylling
 */
public class ExceptionTransformer {

	public static SAXParseException newSAXParseException(Exception e){
		LocatorImpl loc = new LocatorImpl();		
		
		try{
			if(e instanceof TransformerException) {			
				TransformerException te = (TransformerException) e;		
				loc.setLineNumber(te.getLocator().getLineNumber());
				loc.setColumnNumber(te.getLocator().getColumnNumber());
				loc.setPublicId(te.getLocator().getPublicId());
				loc.setSystemId(te.getLocator().getSystemId());			
			}
		}catch (Exception e2) {
			//silence
		}

		return new SAXParseException(e.getMessage(), loc, e);
	}
	
}
