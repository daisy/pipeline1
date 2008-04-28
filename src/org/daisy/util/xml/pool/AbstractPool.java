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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A base service layer for pool implementations who rely on hashmap keys
 * @author Markus Gylling
 */
public abstract class AbstractPool {
	protected Map<Map<String,Object>,List<Object>> mProcessorCache = null; 		//key=Map<propsAndFeatures> value=ArrayList<Processor>
	protected static int MAX_CACHE_SIZE = 50;  	//max number of processors*loadfactor in a Map value<ArrayList>
												//there is no limitation on the number of entries in the Map
	private Map<String,Object> mKey = new HashMap<String,Object>(32);		//the features and properties union, functions as key
	
	private boolean mDebug = false;				
			
	protected AbstractPool(){
		mProcessorCache = new HashMap<Map<String,Object>,List<Object>>(MAX_CACHE_SIZE); //we default to this, but allow it to grow
		if(System.getProperty("org.daisy.debug")!=null) mDebug=true;
	}
		
	public void clearCache() {
		this.mProcessorCache.clear();		
	}

	protected Object getProcessorFromCache(Map<String,Object> features, Map<String,Object> properties){					
	    mKey.clear();
	    if(null!=features)mKey.putAll(features);
	    if(null!=properties)mKey.putAll(properties);	    
		List<Object> list = mProcessorCache.get(mKey);
								
		if (list != null && list.size()>0){
			//there was a list of processors implementing the map props
			//and it was not empty
			
			//check the size and adjust if necessary
			if (list.size() > MAX_CACHE_SIZE) {
				do {
					if(mDebug) System.out.println("DEBUG: AbstractPool.getProcessorFromCache decreasing cache size from" + list.size());
					list.remove(list.size() - 1);					
				}while(list.size() > MAX_CACHE_SIZE);
			}
			
			//detach and return a processor
			if(mDebug) System.out.println("DEBUG: AbstractPool.getProcessorFromCache returning processor from existing map");
			return list.remove(list.size() - 1);
		}	
		//yet no processors implementing the map props
		//or none of them available at the moment
		//if the first case, add a new map key 
		//and prepare the cache for the release call:
		if (list == null) mProcessorCache.put(mKey,new ArrayList<Object>());
		if(mDebug) System.out.println("DEBUG: AbstractPool.getProcessorFromCache no processor in map, returning null");
		//return null to let subclass instantiate specific type
		return null;	
	}
	
	public void release(Object processor, Map<String,Object> features, Map<String,Object> properties) {			
		mKey.clear();
		if(null!=features)mKey.putAll(features);
		if(null!=properties)mKey.putAll(properties);	
		List<Object> list = mProcessorCache.get(mKey);
		if(list!=null) {
			if(mDebug) System.out.println("DEBUG: AbstractPool.release to existing list, size before:" + list.size() );
			list.add(processor);
		}else{
			//features or properties of the processor
			//changed between the acquire and the release events
			if(mDebug) System.out.println("DEBUG: AbstractPool.release to nonexisting list" );
			//create a new processor map entry
			mProcessorCache.put(mKey,new ArrayList<Object>());			
			//try again
			release(processor,features,properties);
		}
	}
	
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
