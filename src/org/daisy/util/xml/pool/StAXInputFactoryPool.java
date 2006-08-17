package org.daisy.util.xml.pool;

import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;

/**
 * A singleton StAX XMLInputFactory pool. Used for performance optimization.
 * @author Markus Gylling
 */
public class StAXInputFactoryPool extends AbstractPool {
	
	protected static StAXInputFactoryPool instance = new StAXInputFactoryPool();

	static public StAXInputFactoryPool getInstance() {
		return instance;
	}

	private StAXInputFactoryPool() {
		super();
	}

	/**
	 * Retrieve an XMLInputFactory from the pool.
	 * <p>If the properties map is null, a XMLInputFactory using the default configuration of the particular implementation will be returned.</p>
	 * <p>XMLInputFactory instances retrieved through the acquire() method are returned to pool using the release() method.</p>
	 */
	public XMLInputFactory acquire(Map properties) throws PoolException {
		try {
			Object o = getProcessorFromCache(null,properties);
			if(o!=null) {
				return (XMLInputFactory)o;
			}
			return create(properties);			
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}
	}
	
	/**
	 * Return the XMLInputFactory back to the pool
	 * @param xif The factory that is to be returned
	 * @param properties The property map used as inparam to the acquire method
	 */
	public void release(XMLInputFactory xif, Map properties) throws PoolException {		  		
		try {			
			//reset all handlers
			xif.setEventAllocator(null);
			xif.setXMLReporter(null);
			xif.setXMLResolver(null);
			super.release(xif, null, properties);
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}
	}
	
	/**
	 * Creates a brand new XMLInputFactory when super does not carry one in the cache
	 * @throws PoolException 
	 */
	private XMLInputFactory create(Map properties) throws PoolException {
		XMLInputFactory xif = XMLInputFactory.newInstance();	    	    
	    return setProperties(xif,properties);		
	}
	
	private XMLInputFactory setProperties(XMLInputFactory xif, Map properties) throws PoolException {	     
	    if (properties != null){
	      for (Iterator i = properties.keySet().iterator(); i.hasNext();){
	        String property = (String)i.next();
	        if(xif.isPropertySupported(property)) {
	        	xif.setProperty(property, properties.get(property));
	        }else{
	        	throw new PoolException("Property not supported on inputfactory: " + property);
	        }
	      }
	    }
	    return xif;
	}
	
}


//defaultProperties.put(XMLInputFactory.ALLOCATOR, xmlInputFactory.getEventAllocator());
//defaultProperties.put(XMLInputFactory.IS_COALESCING, xmlInputFactory.getProperty(XMLInputFactory.IS_COALESCING));
//defaultProperties.put(XMLInputFactory.IS_NAMESPACE_AWARE, xmlInputFactory.getProperty(XMLInputFactory.IS_NAMESPACE_AWARE));
//defaultProperties.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, xmlInputFactory.getProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES));
//defaultProperties.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, xmlInputFactory.getProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES));
//defaultProperties.put(XMLInputFactory.IS_VALIDATING, xmlInputFactory.getProperty(XMLInputFactory.IS_VALIDATING));
//defaultProperties.put(XMLInputFactory.REPORTER, xmlInputFactory.getXMLReporter());
//defaultProperties.put(XMLInputFactory.RESOLVER, xmlInputFactory.getXMLResolver());
//defaultProperties.put(XMLInputFactory.SUPPORT_DTD, xmlInputFactory.getProperty(XMLInputFactory.SUPPORT_DTD));