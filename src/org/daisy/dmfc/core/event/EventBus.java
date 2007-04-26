package org.daisy.dmfc.core.event;

import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A singleton event bus.
 * @author Romain Deltour
 * @author Markus Gylling 
 */
public class EventBus {

	//use class-level instantiation to avoid the dreaded singleton thread probs	
    private Map<Class<? extends EventObject>, Set<BusListener>> mListenersMap 
    	= new HashMap<Class<? extends EventObject>, Set<BusListener>>();
    
    private static EventBus mInstance = new EventBus();

    
    private EventBus() {
    
    }

    /**
     * Singleton instance.
     */
    public static EventBus getInstance() {
        return mInstance;
    }

    
    /**
     * Subscribe as a recipient of events from the EventBus.
     * @param subscriber The object that is subscribing.
     * @param type The of Event object to subscribe to. Registering 
     * a class that has subclasses infers subscription to these subclasses as well.
     */    
    public void subscribe(BusListener subscriber, Class<? extends EventObject> type) {
    	
    	//add the subscriber to a set that represents the event     	    	
        Set<BusListener> listeners = mListenersMap.get(type);
        if (listeners==null) {
            listeners = new HashSet<BusListener>();
            mListenersMap.put(type, listeners);
        }
        listeners.add(subscriber);
                
//        //add the subscriber to any sets that represent subclasses of the event 
//        for (Iterator iter = mListenersMap.keySet().iterator(); iter.hasNext();) {
//			Class klass = (Class) iter.next();
//			if (isSubclass(type, klass)) {
//				Set<BusListener> set = mListenersMap.get(klass);
//				set.add(subscriber);
//			}
//		}
        
    }

    
    /**
     * Unsubscribe as a receiver of events from the EventBus.
     * @param subscriber The object that will stop recieving events.
     * @param type The of Event object to unsubscribe from. Unsubscribing 
     * a class that has subclasses infers unsubscription from these subclasses as well.
     */
    public void unsubscribe(BusListener subscriber, Class<? extends EventObject> type) {
    	//unsubscribe from the event
    	Set<BusListener> listeners = mListenersMap.get(type);
    	if(listeners!=null)listeners.remove(subscriber);
    	
//    	//unsubscribe from any subclasses of the event
//        for (Iterator iter = mListenersMap.keySet().iterator(); iter.hasNext();) {
//			Class klass = (Class) iter.next();
//			if (isSubclass(klass,type)) {
//				Set<BusListener> set = mListenersMap.get(klass);
//				set.remove(subscriber);
//			}
//		}
    	
    }
    
    /**
     * Publish an event in the event bus. Subscribers that have subscribed to superclasses
     * of the published event will recieve notification as well.
     */
    public void publish(EventObject event) {   

    	//publish to subscribers of the event 
        Set<BusListener> listeners = mListenersMap.get(event.getClass());
        publish(listeners,event);
        
        //publish to subscribers of superclasses of the event
        for (Class klass : mListenersMap.keySet())  {			
			if (isSubclass(event.getClass(),klass)) {
				Set<BusListener> set = mListenersMap.get(klass);
				publish(set,event);				
			}
		}                
    }
    
    private void publish(Set<BusListener>listeners,EventObject event) {
        if(listeners!=null){
	        for (BusListener listener : listeners) {        	
	            listener.received(event);
	        }
        }
    	
    }
    
    /**
     * @return true if class compare is a subclass of class to. 
     */
    private boolean isSubclass(Class compare, Class to) {		
		if (compare == null || to == null) return false;	
		for (Class c = compare.getSuperclass(); c != null; c = c.getSuperclass()) {
		    if (c == to) return true;		    
		}
		return false;
    }
    
}
