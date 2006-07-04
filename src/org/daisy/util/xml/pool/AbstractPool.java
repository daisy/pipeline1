package org.daisy.util.xml.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Markus Gylling
 */
public abstract class AbstractPool {
	protected Map processorCache = null; 		//key=Map<propsAndFeatures> value=ArrayList<Processor>
	protected static int MAX_CACHE_SIZE = 50;  	//max number of processors in a Map value<ArrayList>
												//there is no limitation on the number of entries in the Map
			
	protected AbstractPool(){
		processorCache = new HashMap(MAX_CACHE_SIZE);
	}
		
	public void clearCache() {
		this.processorCache.clear();		
	}

	protected Object getProcessorFromCache(Map features, Map properties){				
	    Map key = new HashMap();
	    if(null!=features)key.putAll(features);
	    if(null!=properties)key.putAll(properties);	    
		ArrayList list = (ArrayList)processorCache.get(key);
								
		if (list != null && list.size()>0){
			//there was a list of processors implementing the map props
			//and it was not empty
			
			//check the size and adjust if necessary
			if (list.size() > MAX_CACHE_SIZE) {
				do {
					list.remove(list.size() - 1);					
				}while(list.size() > MAX_CACHE_SIZE);
			}
			
			//detach and return a processor
			return list.remove(list.size() - 1);
		}	
		//yet no processors implementing the map props
		//or none of them available at the moment
		//if the first case, add a new map key 
		//and prepare the cache for the release call:
		if (list == null) processorCache.put(key,new ArrayList());
		//return null to let subclass instantiate specific type
		return null;	
	}
	
	public void release(Object processor, Map features, Map properties) {		
		Map key = new HashMap();
		if(null!=features)key.putAll(features);
		if(null!=properties)key.putAll(properties);	
		ArrayList list = (ArrayList)processorCache.get(key);		    		    
		list.add(processor);		    	    	    	
	}
	
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
