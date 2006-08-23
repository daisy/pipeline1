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

package org.daisy.util.xml.pool;

import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * A SAXParser pool. Used for performance optimization.
 * @author Markus Gylling
 */
public class SAXParserPool extends AbstractPool {
    private static SAXParserFactory saxParserFactory =  null;
    protected static SAXParserPool instance = new SAXParserPool();	
	
    
	static public SAXParserPool getInstance() {
		return instance;
	}
	
	private SAXParserPool(){
		super();
		saxParserFactory = SAXParserFactory.newInstance();
		saxParserFactory.setNamespaceAware(true);
		saxParserFactory.setValidating(false);
	}
	
	/**
	 * Retrieve a SAXParser from the pool.
	 * <p>If the features and properties maps are null, a namespaceaware nonvalidating parser will be returned.</p>
	 * <p>Parser instances retrieved through the acquire() method are returned to pool using the release() method.</p>
	 * <p>Property and feature maps can be populated using {@link org.daisy.util.xml.sax.SAXConstants}.</p>
	 * 
	 * <p>For the official SAX features and properties list, see
	 * http://www.saxproject.org/apidoc/org/xml/sax/package-summary.html#package_description</p>
	 * 
	 * <p>For Xerces specific properties, see
	 * http://xerces.apache.org/xerces2-j/properties.html</p>
	 * 
	 * <p>For Xerces specific features, see 
	 * http://xerces.apache.org/xerces2-j/features.html</p>
	 */
	
	public SAXParser acquire(Map features, Map properties) throws PoolException {
		try {
			Object o = getProcessorFromCache(features, properties);
			if(o!=null) {
				return (SAXParser)o;
			}
			return create(features, properties);			
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}		
	}

	/**
	 * Return the parser back to the pool.
	 * @param parser The SAXParser that is to be returned
	 * @param features The feature map used as inparam to the acquire method
	 * @param properties The property map used as inparam to the acquire method
	 */
	public void release(SAXParser parser, Map features, Map properties) throws PoolException {		  		
		try {			
			//reset all handlers
			parser.getXMLReader().setContentHandler(null);
			parser.getXMLReader().setDTDHandler(null);
			parser.getXMLReader().setEntityResolver(null);
			parser.getXMLReader().setErrorHandler(null);	
			//call reset, repop and release
			parser.reset(); 
			super.release(setFeaturesAndProperties(parser,features, properties), features, properties);
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}
	}
	
	/**
	 * Creates a brand new parser when super does not carry one in the cache
	 */
	private SAXParser create(Map features, Map properties) throws ParserConfigurationException, SAXException {
	    SAXParser parser = saxParserFactory.newSAXParser();	    	    
	    return setFeaturesAndProperties(parser,features,properties);		
	}
	
	private SAXParser setFeaturesAndProperties(SAXParser parser, Map features, Map properties) throws SAXNotRecognizedException, SAXNotSupportedException, SAXException {
	    Iterator i;
	    if (features != null){
	      for (i = features.keySet().iterator(); i.hasNext();){
	        String feature = (String)i.next();
	        parser.getXMLReader().setFeature(feature, ((Boolean)features.get(feature)).booleanValue());
	      }
	    }
	    if (properties != null){
	      for (i = properties.keySet().iterator(); i.hasNext();){
	        String property = (String)i.next();
	        parser.getXMLReader().setProperty(property, properties.get(property));
	      }
	    }
	    return parser;
	}
}

