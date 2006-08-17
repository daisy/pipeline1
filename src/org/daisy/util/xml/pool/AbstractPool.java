package org.daisy.util.xml.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A base service layer for pool implementations who rely on hashmap keys
 * @author Markus Gylling
 */
public abstract class AbstractPool {
	protected Map processorCache = null; 		//key=Map<propsAndFeatures> value=ArrayList<Processor>
	protected static int MAX_CACHE_SIZE = 50;  	//max number of processors*loadfactor in a Map value<ArrayList>
												//there is no limitation on the number of entries in the Map
	private HashMap key = new HashMap(32);		//the features and properties union, functions as key
	
	private boolean debug = false;				
			
	protected AbstractPool(){
		processorCache = new HashMap(MAX_CACHE_SIZE); //we default to this, but allow it to grow
		if(System.getProperty("org.daisy.debug")!=null) debug=true;
	}
		
	public void clearCache() {
		this.processorCache.clear();		
	}

	protected Object getProcessorFromCache(Map features, Map properties){					
	    key.clear();
	    if(null!=features)key.putAll(features);
	    if(null!=properties)key.putAll(properties);	    
		ArrayList list = (ArrayList)processorCache.get(key);
								
		if (list != null && list.size()>0){
			//there was a list of processors implementing the map props
			//and it was not empty
			
			//check the size and adjust if necessary
			if (list.size() > MAX_CACHE_SIZE) {
				do {
					if(debug) System.out.println("DEBUG: AbstractPool.getProcessorFromCache decreasing cache size from" + list.size());
					list.remove(list.size() - 1);					
				}while(list.size() > MAX_CACHE_SIZE);
			}
			
			//detach and return a processor
			if(debug) System.out.println("DEBUG: AbstractPool.getProcessorFromCache returning processor from existing map");
			return list.remove(list.size() - 1);
		}	
		//yet no processors implementing the map props
		//or none of them available at the moment
		//if the first case, add a new map key 
		//and prepare the cache for the release call:
		if (list == null) processorCache.put(key,new ArrayList());
		if(debug) System.out.println("DEBUG: AbstractPool.getProcessorFromCache no processor in map, returning null");
		//return null to let subclass instantiate specific type
		return null;	
	}
	
	public void release(Object processor, Map features, Map properties) {			
		key.clear();
		if(null!=features)key.putAll(features);
		if(null!=properties)key.putAll(properties);	
		ArrayList list = (ArrayList)processorCache.get(key);
		if(list!=null) {
			if(debug) System.out.println("DEBUG: AbstractPool.release to existing list" );
			list.add(processor);
		}else{
			//features or properties of the processor
			//changed between the acquire and the release events
			if(debug) System.out.println("DEBUG: AbstractPool.release to nonexisting list" );
			//create a new processor map entry
			processorCache.put(key,new ArrayList());			
			//try again
			release(processor,features,properties);
		}
	}
	
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
