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
 * @author markusg
 */

public class XmlReaderCreatorImpl implements XMLReaderCreator{
	//static SAXParserFactory factory = SAXParserFactory.newInstance();
	
	public XmlReaderCreatorImpl() {
		
	}
	
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
	    }
	    catch (SAXNotSupportedException e) {
	    }
	    return xr;

		
		//		XMLReader xr = null;
//		try {
//			xr = factory.newSAXParser().getXMLReader();			
//			xr.setEntityResolver(CatalogEntityResolver.getInstance());
//			xr.setFeature("http://xml.org/sax/features/namespaces", true);
//			xr.setFeature("http://xml.org/sax/features/namespace-prefixes", false);						
//			xr.setFeature("http://xml.org/sax/features/validation", false);						
//		} catch (SAXException se) {
//			System.err.println("SAXException se");
//		} catch (ParserConfigurationException pce) {
//			System.err.println("ParserConfigurationException pce");
//		}
//		System.err.println(xr.getClass().getName());
//		return xr;		
	}
}



