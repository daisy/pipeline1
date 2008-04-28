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
package org.daisy.util.xml.peek;

import java.util.HashMap;
import java.util.Map;

import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.SAXParserPool;
import org.daisy.util.xml.sax.SAXConstants;

/**
 * A singleton pool for retrieval of Peeker objects.
 * @author Markus Gylling
 */

/* 
 *  We use the pool pattern in an attempt to maximize performance,
 *  although actually Peekers arent pooled, only their underlying
 *  SAXParsers. So this is basically a masquerade wrapper using 
 *  a real SAXParserPool to feed semi-recycled Peeker implementations to the world.
 */

public class PeekerPool {
	private static Map<String,Object> mFeatures = null; 		//static features of a peeker saxparser
	private static Map<String,Object> mProperties = null; 		//static properties of a peeker saxparser
	private static PeekerPool mInstance = new PeekerPool(); 	//singleton instance

	/**
	 * Constructor.
	 */
	private PeekerPool() {
		//configure the underlying SAXParser feature map
		//config goal is to support DTD load off, and instead have 
		//lexical handler report the prolog pub+sys ids
		mFeatures = new HashMap<String,Object>();
		//these features never vary between Peeker instances:
		mFeatures.put(SAXConstants.SAX_FEATURE_VALIDATION, Boolean.FALSE);
		mFeatures.put(SAXConstants.SAX_FEATURE_EXTERNAL_GENERAL_ENTITIES, Boolean.FALSE);
		mFeatures.put(SAXConstants.SAX_FEATURE_EXTERNAL_PARAMETER_ENTITIES, Boolean.FALSE);
		mFeatures.put(SAXConstants.SAX_FEATURE_LEXICAL_HANDLER_PARAMETER_ENTITIES, Boolean.TRUE);
		mFeatures.put(SAXConstants.SAX_FEATURE_NAMESPACES, Boolean.TRUE);
		mFeatures.put(SAXConstants.SAX_FEATURE_NAMESPACE_PREFIXES, Boolean.TRUE);
		mFeatures.put(SAXConstants.SAX_FEATURE_STRING_INTERNING, Boolean.TRUE);
	}

	/**
	 * Retrieve access to the pool singleton.
	 */
	static public PeekerPool getInstance() {
		return mInstance;
	}

	/**
	 * Retrieve a Peeker object from the pool.
	 * <p>If the underlying SAXParser is Apache Xerces, the returned peeker will have DTD loading turned off.</p>
	 * @see #acquire(boolean) 
	 */
	public Peeker acquire() throws PoolException {
		return acquire(true);
	}

	/**
	 * Retrieve a Peeker object from the pool.
	 * @param apacheIgnoreDTD if false, DTD loading (and consequently attribute defaulting) will be enabled.
	 * <p>If true, DTD loading (and consequently attribute defaulting) will be disabled. This latter mode requires 
	 * Apache Xerces, since Apache specific SAX features are used. If Apache Xerces is not available in the runtime, 
	 * or the Apache specific SAX features are not recognized by the JAXP allocated SAXParser implementation, 
	 * a peeker will still be returned, but with DTD loading most likely enabled.</p>
	 * <p>Note - in neither case will DTD validation be turned on.</p>
	 */
	public Peeker acquire(boolean apacheIgnoreDTD) throws PoolException {
		
		//set the two features that vary depending on user inparam
		if(apacheIgnoreDTD) {
			mFeatures.put(SAXConstants.APACHE_FEATURE_LOAD_DTD_GRAMMAR, Boolean.FALSE);
			mFeatures.put(SAXConstants.APACHE_FEATURE_LOAD_EXTERNAL_DTD, Boolean.FALSE);
		}else{
			mFeatures.put(SAXConstants.APACHE_FEATURE_LOAD_DTD_GRAMMAR, Boolean.TRUE);
			mFeatures.put(SAXConstants.APACHE_FEATURE_LOAD_EXTERNAL_DTD, Boolean.TRUE);			
		}
				
		try {
			return new PeekerImpl(SAXParserPool.getInstance().acquire(mFeatures, mProperties));
		} catch (PoolException pe) {
			//a feature or a prop prolly threw a SAXNotSupportedException			
			if (System.getProperty("org.daisy.debug") != null) {
				System.out.println("DEBUG: PeekerPool#acquire PoolException");
			}			
			//try a second time with only org.xml.sax features put
			try {
				mFeatures.remove(SAXConstants.APACHE_FEATURE_LOAD_DTD_GRAMMAR);
				mFeatures.remove(SAXConstants.APACHE_FEATURE_LOAD_EXTERNAL_DTD);
				return new PeekerImpl(SAXParserPool.getInstance().acquire(mFeatures, mProperties));
			} catch (PoolException pe2) {
				//try one last time with no features or props at all
				return new PeekerImpl(SAXParserPool.getInstance().acquire(null, null));
			}
		}
				
	}
	
	/**
	 * Return a Peeker object back to the pool.
	 * @throws PoolException 
	 */
	public void release(Peeker peeker) throws PoolException {
		PeekerImpl pimp = (PeekerImpl) peeker;		
		try {
			//since we dont know the loadDTD status of incoming peeker, adjust the map before releasing
			mFeatures.put(SAXConstants.APACHE_FEATURE_LOAD_DTD_GRAMMAR, 
					Boolean.valueOf(pimp.getSAXParser().getXMLReader().getFeature(SAXConstants.APACHE_FEATURE_LOAD_DTD_GRAMMAR)));
			mFeatures.put(SAXConstants.APACHE_FEATURE_LOAD_EXTERNAL_DTD, 
					Boolean.valueOf(pimp.getSAXParser().getXMLReader().getFeature(SAXConstants.APACHE_FEATURE_LOAD_EXTERNAL_DTD)));
		} catch (Exception e) {
			if (System.getProperty("org.daisy.debug") != null) {
				System.out.println("DEBUG: PeekerPool#release Exception");
			}
		}		
		SAXParserPool.getInstance().release(pimp.getSAXParser(), mFeatures, mProperties);
	}
}
