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
 * <p>Uses the singleton approach described in http://www-128.ibm.com/developerworks/java/library/j-dcl.html.</p>
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
	 * return the parser back to the pool
	 */
	public void release(SAXParser parser, Map features, Map properties) throws PoolException {		  		
		try {
			//TODO revisit: cost?
			//reset to release all handlers et al
			parser.reset();
			//then repop with identity features, and call release
			super.release(setFeaturesAndProperties(parser,features,properties), features, properties);
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}
	}
	
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

