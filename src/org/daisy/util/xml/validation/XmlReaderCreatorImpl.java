package org.daisy.util.xml.validation;

//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.parsers.SAXParserFactory;

import org.daisy.util.xml.dtdcatalog.CatalogEntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import com.thaiopensource.xml.sax.XMLReaderCreator;

/**
 * Custom implementation of Clarks XMLReaderCreator, supporting the set of {@link org.daisy.util.xml.dtdcatalog.CatalogEntityResolver}
 * @author markusg
 */

public class XmlReaderCreatorImpl implements XMLReaderCreator{
	
	public XmlReaderCreatorImpl() {}
	
	public XMLReader createXMLReader() throws SAXException {		
	    XMLReader xr;	    
	    xr = XMLReaderFactory.createXMLReader();	    
	    xr.setFeature("http://xml.org/sax/features/namespaces", true);
	    xr.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
	    xr.setEntityResolver(CatalogEntityResolver.getInstance());
	    try {
	      xr.setFeature("http://xml.org/sax/features/validation", false);
	    }
	    catch (SAXNotRecognizedException e) {
	    	System.err.println("caught SAXNotRecognizedException in  org.daisy.util.validation.XmlReaderCreatorImpl");
	    }
	    catch (SAXNotSupportedException e) {
	    	System.err.println("caught SAXNotSupportedException in  org.daisy.util.validation.XmlReaderCreatorImpl");
	    }
	    
	    return xr;		
	}
}