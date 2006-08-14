package org.daisy.util.xml.pool;

import java.util.Iterator;
import java.util.Map;

import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * A singleton DOMParser pool. Used for performance optimization.
 * <p> Uses the singleton approach
 * described in http://www-128.ibm.com/developerworks/java/library/j-dcl.html.</p>
 * <p>Do not use this class if you cannot guarantee the availability of 
 * <code>org.apache.xerces.parsers.DOMParser</code> on your classpath; 
 * this implementation does not use the JAXP abstractfactory API.</p>
 * @author Markus Gylling
 */
public class DOMParserPool extends AbstractPool {
	protected static DOMParserPool instance = new DOMParserPool();	
		
	static public DOMParserPool getInstance() {
		return instance;
	}
	
	private DOMParserPool(){
		super();
	}
	
	/**
	 * Retrieve a DOMParser from the pool, configurable via SAX properties. Do not use this class
	 * if you cannot guarantee the availability of <code>org.apache.xerces.parsers.DOMParser</code>
	 * on your classpath.
	 * <p>If the features and properties maps are null, a namespaceaware nonvalidating parser will be returned.</p>
	 * <p>Parser instances retrieved through the acquire() method are returned to pool using the release() method.</p>
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
	public DOMParser acquire(Map saxFeatures, Map saxProperties) throws PoolException {
		try {
			Object o = getProcessorFromCache(saxFeatures, saxProperties);
			if(o!=null) {
				return (DOMParser)o;
			}
			return createDomParser(saxFeatures, saxProperties);		
			
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}		
	}

	/**
	 * return the parser back to the pool
	 */
	public void release(DOMParser parser, Map features, Map properties) {
		parser.reset();		
		super.release(parser, features, properties);
	}
	
	private DOMParser createDomParser(Map features, Map properties) throws ClassCastException, SAXNotRecognizedException, SAXNotSupportedException {
		
		DOMParser parser = new DOMParser();
				
	    Iterator i;
	    if (features != null){
	      for (i = features.keySet().iterator(); i.hasNext();){
	        String feature = (String)i.next();
	        parser.setFeature(feature, ((Boolean)features.get(feature)).booleanValue());
	      }
	    }
	    if (properties != null){
	      for (i = properties.keySet().iterator(); i.hasNext();){
	        String property = (String)i.next();
	        parser.setProperty(property, properties.get(property));
	      }
	    }
	    
	    return parser;
		
	}
}
