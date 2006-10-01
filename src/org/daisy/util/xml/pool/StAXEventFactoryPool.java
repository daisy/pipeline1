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

import javax.xml.stream.XMLEventFactory;

/**
 * A singleton StAX XMLEventFactory pool. Used for performance optimization.
 * @author Markus Gylling
 */
public class StAXEventFactoryPool extends AbstractPool {
	
	protected static StAXEventFactoryPool mInstance = new StAXEventFactoryPool();

	static public StAXEventFactoryPool getInstance() {
		return mInstance;
	}

	private StAXEventFactoryPool() {
		super();
	}

	/**
	 * Retrieve an XMLEventFactory from the pool.
	 * <p>XMLEventFactory instances retrieved through the acquire() method are returned to pool using the release() method.</p>
	 */
	public XMLEventFactory acquire() throws PoolException {
		try {
			Object o = getProcessorFromCache(null,null);
			if(o!=null) {
				return (XMLEventFactory)o;
			}
			return create();			
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}
	}
	
	/**
	 * Return the XMLEventFactory back to the pool
	 * @param xef The factory that is to be returned
	 */
	public void release(XMLEventFactory xef) throws PoolException {		  		
		try {			
			//reset all handlers
			xef.setLocation(null);
			super.release(xef, null, null);
		} catch (Exception e) {
			throw new PoolException(e.getMessage(),e);
		}
	}
	
	/**
	 * Creates a brand new XMLEventFactory when super does not carry one in the cache
	 */
	private XMLEventFactory create() {
		return XMLEventFactory.newInstance();				
	}
		
}
