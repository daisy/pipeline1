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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;

/**
 * A singleton StAX XMLOutputFactory pool. Used for performance optimization.
 * @author Markus Gylling
 */
public class StAXOutputFactoryPool extends AbstractPool {
	
	protected static StAXOutputFactoryPool mInstance = new StAXOutputFactoryPool();
	private static Map<String, Object> mDefaultPropertyMap = null;

	static public StAXOutputFactoryPool getInstance() {
		return mInstance;
	}

	private StAXOutputFactoryPool() {
		super();
	}

	/**
	 * Retrieve an XMLOutputFactory from the pool.
	 * <p>If the properties map is null, a XMLOutputFactory using the default configuration of the particular implementation will be returned.</p>
	 * <p>XMLOutputFactory instances retrieved through the acquire() method are returned to pool using the release() method.</p>
	 */
	public XMLOutputFactory acquire(Map properties) throws PoolException {
		try {
			Object o = getProcessorFromCache(null,properties);
			if(o!=null) {
				return (XMLOutputFactory)o;
			}
			return create(properties);			
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}
	}
	
	/**
	 * Return the XMLOutputFactory back to the pool
	 * @param xof The factory that is to be returned
	 * @param properties The property map used as inparam to the acquire method
	 */
	public void release(XMLOutputFactory xof, Map properties) throws PoolException {		  		
		try {			
			super.release(xof, null, properties);
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}
	}
	
	/**
	 * Creates a brand new XMLInputFactory when super does not carry one in the cache
	 * @throws PoolException 
	 */
	private XMLOutputFactory create(Map properties) throws PoolException {
		XMLOutputFactory xof = XMLOutputFactory.newInstance();	    	    
	    return setProperties(xof,properties);		
	}
	
	private XMLOutputFactory setProperties(XMLOutputFactory xof, Map properties) throws PoolException {	     
	    if (properties != null){
	      for (Iterator i = properties.keySet().iterator(); i.hasNext();){
	        String property = (String)i.next();
	        if(xof.isPropertySupported(property)) {
	        	xof.setProperty(property, properties.get(property));
	        }else{
	        	throw new PoolException("Property not supported on outputfactory: " + property);
	        }
	      }
	    }
	    return xof;
	}
	
	/**
	 * Convenience method to get a standard property map
	 */
	public Map<String, Object> getDefaultPropertyMap(){
		
		if(null== mDefaultPropertyMap) {
			mDefaultPropertyMap = new HashMap<String, Object>();
			mDefaultPropertyMap.put(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
			//need to ballet a bit to find out if we have woodstox
			XMLOutputFactory xof = null;
			try{
				xof = getInstance().acquire(null);
				if(xof.isPropertySupported("com.ctc.wstx.outputEscapeCr")) {
					try{
						mDefaultPropertyMap.put("com.ctc.wstx.outputEscapeCr", Boolean.FALSE);
					}catch (IllegalArgumentException e) {
						
					}	
				}	
			}finally{
				getInstance().release(xof, null);
			}
		}		
		return mDefaultPropertyMap;
	}
		
}