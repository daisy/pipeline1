package org.daisy.dmfc.core.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @author Romain Deltour
 */
public interface BusListener extends EventListener {
	
	/**
	 * Recieve an EventObject from the EventBus.
	 */
    void recieved(EventObject event);
}
