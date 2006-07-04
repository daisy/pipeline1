package org.daisy.util.xml.validation;

//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.parsers.SAXParserFactory;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import com.thaiopensource.xml.sax.XMLReaderCreator;

/**
 * Custom implementation of Clarks XMLReaderCreator, supporting the set of {@link org.daisy.util.xml.catalog.CatalogEntityResolver}
 * @author markusg
 */

public class XmlReaderCreatorImpl implements XMLReaderCreator{
	private boolean loadDTD = true;
	
	public XmlReaderCreatorImpl() {}
	
	
	public XmlReaderCreatorImpl(boolean loadDTD) {
	  this.loadDTD = loadDTD;
	}
	
	public XMLReader createXMLReader() throws SAXException {		
	    XMLReader xr = null;	    
	    try {
	      xr = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
	    } catch(SAXException se) {
	    	try {
	    		xr = XMLReaderFactory.createXMLReader("com.sun.org.apache.xerces.internal.parsers.SAXParser");
	    	} catch(SAXException se2) {
	    		xr = XMLReaderFactory.createXMLReader();
	    	}	
	    }
	    
	    try {
	      xr.setEntityResolver(CatalogEntityResolver.getInstance());	
	      xr.setFeature("http://xml.org/sax/features/namespaces", true);
	      xr.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
	      xr.setFeature("http://xml.org/sax/features/validation", false);
	      if(!loadDTD) {
	        xr.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
	        xr.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);	      	    
	      }
	    }
	    catch (SAXNotRecognizedException e) {
	    	System.err.println("caught SAXNotRecognizedException in org.daisy.util.validation.XmlReaderCreatorImpl");
	    }
	    catch (SAXNotSupportedException e) {
	    	System.err.println("caught SAXNotSupportedException in org.daisy.util.validation.XmlReaderCreatorImpl");
	    }
	    
	    return xr;		
	}
}