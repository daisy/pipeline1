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

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * A singleton LSSerializer pool. Used for performance optimization.
 * @author Markus Gylling
 */
public class LSSerializerPool extends AbstractPool {
	private static DOMImplementationLS mDOMImplementationLS = null;
	protected static LSSerializerPool mInstance = new LSSerializerPool();	
		
	static public LSSerializerPool getInstance() {
		return mInstance;
	}
	
	private LSSerializerPool(){
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
	 */
	public LSSerializer acquire() throws PoolException {
		try {
			Object o = getProcessorFromCache(null, null);
			if(o!=null) {
				return (LSSerializer)o;
			}
			return createLSSerializer();		
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}		
	}

	/**
	 * Return the serializer back to the pool
	 * @throws PoolException 
	 */
	public void release(LSSerializer serializer) throws PoolException {	
		try {												
			serializer.getDomConfig().setParameter("error-handler", null);
			super.release(serializer,null,null);
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);			
		} 
	}

	public DOMImplementationLS getDOMImplementationLS() {
		return mDOMImplementationLS;
	}
		
	@SuppressWarnings("unused")
	private LSSerializer createLSSerializer() throws ClassCastException, SAXNotRecognizedException, SAXNotSupportedException {		
		LSSerializer serializer = mDOMImplementationLS.createLSSerializer();				    
	    return serializer;		
	}
}
