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
import java.util.Map;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSParser;

/**
 * A singleton LSParser pool. Used for performance optimization.
 * @author Markus Gylling
 */
public class LSParserPool extends AbstractPool {
	private static DOMImplementationLS mDOMImplementationLS = null;
	private static Map<String, Object> mDefaultPropertyMap = null;
	protected static LSParserPool mInstance = new LSParserPool();	
	
		
	static public LSParserPool getInstance() {
		return mInstance;
	}
	
	private LSParserPool(){
		super();		
		DOMImplementationRegistry registry = null;
		try {
			registry = DOMImplementationRegistry.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		} 		
		DOMImplementation domImpl = registry.getDOMImplementation("LS 3.0");
		mDOMImplementationLS = (DOMImplementationLS) domImpl;
	}
	
	/**
	 * Retrieve a LSParser from the pool, configurable via DOMConfiguration. 
	 * <b>Note - set the error-handler property on the dom config after retrieval.</b>
	 * @param domConfigMap key value pairs from http://docjar.com/docs/api/org/w3c/dom/DOMConfiguration.html
	 * @param mode DOMImplementationLS.MODE_SYNCHRONOUS or DOMImplementationLS.MODE_ASYNCHRONOUS 	 
	 */
	public LSParser acquire(Map<String,Object> domConfigMap, short mode) throws PoolException {
		Map<String,Object> features = new HashMap<String,Object>();
				
		features.put("DOMImplementationLS.MODE", Short.valueOf(mode));
						
		try {
			Object o = getProcessorFromCache(features, domConfigMap);
			if(o!=null) {
				return (LSParser)o;				
			}
			return createLSParser(features,domConfigMap);		
			
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}		
		
	}

	/**
	 * Retrieve a LSParser from the pool, configurable via DOMConfiguration.
	 * <p>The parser returned will be synchronous.</p>
	 */
	public LSParser acquire(Map<String,Object> domConfigMap) throws PoolException {
		return this.acquire(domConfigMap, DOMImplementationLS.MODE_SYNCHRONOUS);
	}
	
	private LSParser createLSParser(Map<String,Object> features, Map<String,Object> domConfig)  {		
		Short mode = (Short)features.get("DOMImplementationLS.MODE");
		LSParser parser = mDOMImplementationLS.createLSParser(mode.shortValue(), null);		
		DOMConfiguration config = parser.getDomConfig();		
		for(Object o : domConfig.keySet()) {
			String s = (String)o;
			config.setParameter(s,domConfig.get(s));
		}		
		return parser;
	}
		
	/**
	 * Return the parser back to the pool.
	 * @throws PoolException 
	 */
	public void release(LSParser parser, Map<String,Object> domConfig) throws PoolException {	
		try {
			Map<String,Object> features = new HashMap<String,Object>();
						
			short mode;
			
			if(parser.getAsync()) {
				mode = DOMImplementationLS.MODE_ASYNCHRONOUS;
			}else{
				mode = DOMImplementationLS.MODE_SYNCHRONOUS;
			}			
			features.put("DOMImplementationLS.MODE", Short.valueOf(mode));
						
			//reset error-handler
			parser.getDomConfig().setParameter("error-handler", null);
						
			super.release(parser,features, domConfig);
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);			
		} 
	}
	
	/**
	 * Convenience method to get a standard layout property map
	 * <p>For properties, see http://docjar.com/docs/api/org/w3c/dom/DOMConfiguration.html</p>
	 */
	public Map<String, Object> getDefaultPropertyMap(Boolean dtdValidating){
		if(null==mDefaultPropertyMap) {			
			mDefaultPropertyMap = new HashMap<String, Object>();			
//			try {
//				mDefaultPropertyMap.put("resource-resolver", CatalogEntityResolver.getInstance());				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
		mDefaultPropertyMap.put("validate", dtdValidating);
		mDefaultPropertyMap.put("validate-if-schema", dtdValidating);
		return mDefaultPropertyMap;
	}
	

}
