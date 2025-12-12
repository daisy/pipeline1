/*
 * org.daisy.util (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.util.xml.pool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;

/**
 * A singleton StAX XMLInputFactory pool. Used for performance optimization.
 * @author Markus Gylling
 */
public class StAXInputFactoryPool extends AbstractPool {
	
	protected static StAXInputFactoryPool mInstance = new StAXInputFactoryPool();
	private static Map<String, Object> mDefaultPropertyMap = null;

	static public StAXInputFactoryPool getInstance() {
		return mInstance;
	}

	private StAXInputFactoryPool() {
		super();
	}

	/**
	 * Retrieve an XMLInputFactory from the pool.
	 * <p>If the properties map is null, a XMLInputFactory using the default configuration of the particular implementation will be returned.</p>
	 * <p>XMLInputFactory instances retrieved through the acquire() method are returned to pool using the release() method.</p>
	 */
	public XMLInputFactory acquire(Map<String,Object> properties) throws PoolException {
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
	public void release(XMLInputFactory xif, Map<String,Object> properties) throws PoolException {		  		
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
	private XMLInputFactory create(Map<String,Object> properties) throws PoolException {
		XMLInputFactory xif = XMLInputFactory.newFactory(
			"javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
	    return setProperties(xif,properties);		
	}
	
	private XMLInputFactory setProperties(XMLInputFactory xif, Map<String,Object> properties) throws PoolException {	     
	    if (properties != null){
	      for (Iterator<String> i = properties.keySet().iterator(); i.hasNext();){
	        String property = i.next();
	        if(xif.isPropertySupported(property)) {
	        	xif.setProperty(property, properties.get(property));
	        }else{
	        	throw new PoolException("Property not supported on inputfactory: " + property);
	        }
	      }
	    }
	    return xif;
	}
	
	/**
	 * Convenience method to get a standard layout property map
	 */
	public Map<String, Object> getDefaultPropertyMap(Boolean dtdValidating){
		if(null==mDefaultPropertyMap) {
			mDefaultPropertyMap = new HashMap<String, Object>();
			mDefaultPropertyMap.put(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
			mDefaultPropertyMap.put(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
			mDefaultPropertyMap.put(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.TRUE);
			mDefaultPropertyMap.put(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.TRUE);					
		}
		mDefaultPropertyMap.put(XMLInputFactory.IS_VALIDATING, dtdValidating);
		mDefaultPropertyMap.put(XMLInputFactory.SUPPORT_DTD, dtdValidating);
		return mDefaultPropertyMap;
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