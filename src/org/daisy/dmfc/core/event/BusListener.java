package org.daisy.dmfc.core.event;

import java.util.EventListener;
import java.util.EventObject;

/**
 * @author Romain Deltour
 */
public interface BusListener extends EventListener {
	
	/**
	 * Receive an EventObject from the EventBus.
	 */
    void received(EventObject event);
}
