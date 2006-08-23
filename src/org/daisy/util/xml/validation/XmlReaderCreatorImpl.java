/*
 * org.daisy.util - The DAISY java utility library
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.daisy.util.xml.validation;

//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.parsers.SAXParserFactory;

import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.xml.sax.EntityResolver;
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
	private EntityResolver resolver = null;
	
	public XmlReaderCreatorImpl() {}
	
	
	public XmlReaderCreatorImpl(boolean loadDTD) {
	  this.loadDTD = loadDTD;
	}

	public XmlReaderCreatorImpl(boolean loadDTD, EntityResolver resolver) {
		  this.loadDTD = loadDTD;
		  this.resolver = resolver;
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
	      if(this.resolver==null) {
	    	  xr.setEntityResolver(CatalogEntityResolver.getInstance());
	      }else{
	    	  xr.setEntityResolver(this.resolver);
	      }
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