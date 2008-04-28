/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package org.daisy.pipeline.core.event;

import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A singleton event bus.
 * 
 * @author Romain Deltour
 * @author Markus Gylling
 */
public class EventBus {

	// use class-level instantiation to avoid the dreaded singleton thread probs
	private Map<Class<? extends EventObject>, Set<BusListener>> mListenersMap = new HashMap<Class<? extends EventObject>, Set<BusListener>>();

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
	 * Subscribes a listener as a recipient of events from the EventBus (it will
	 * receive notifications on all the events inheriting from the given class).
	 * <p>
	 * <strong>WARNING: </strong> To avoid memory leaks, the caller of this
	 * method (usually the owner of the <code>listener</code>)
	 * <em>must be sure to unsubscribe the <code>listener</code></em> at
	 * some point using {@link #unsubscribe(BusListener, Class)}.
	 * </p>
	 * 
	 * <p>
	 * Note also that the TransformerHandler class handles subscribing and
	 * unsubscribing main Transformer instances; as a Transformer developer, you
	 * only need to worry about sub- and unsubscribing delegates.
	 * </p>
	 * 
	 * <p>
	 * (The TransformerDelegateListener interface can be used by Transformer
	 * delegates to get access to the event framework without the need for
	 * subscription.)
	 * </p>
	 * 
	 * @param listener
	 *            The object that is subscribing.
	 * @param type
	 *            The class of event to subscribe to.
	 */
	public void subscribe(BusListener listener,
			Class<? extends EventObject> type) {

		// add the subscriber to a set that represents the event
		Set<BusListener> listeners = mListenersMap.get(type);
		if (listeners == null) {
			listeners = new HashSet<BusListener>();
			mListenersMap.put(type, listeners);
		}
		listeners.add(listener);

		// //add the subscriber to any sets that represent subclasses of the
		// event
		// for (Iterator iter = mListenersMap.keySet().iterator();
		// iter.hasNext();) {
		// Class klass = (Class) iter.next();
		// if (isSubclass(type, klass)) {
		// Set<BusListener> set = mListenersMap.get(klass);
		// set.add(subscriber);
		// }
		// }

	}

	/**
	 * Unsubscribes the given listener as a receiver of the given class of
	 * events from the EventBus.
	 * <p>
	 * Note that unsubscribing to a class of event does <em>not</em>infers
	 * unsubscription from subclasses (if such subscription ever occurred).
	 * </p>
	 * 
	 * @param listener
	 *            The object that will stop receiving events.
	 * @param type
	 *            The class of event to unsubscribe from.
	 */
	public void unsubscribe(BusListener listener,
			Class<? extends EventObject> type) {
		// unsubscribe from the event
		Set<BusListener> listeners = mListenersMap.get(type);
		if (listeners != null)
			listeners.remove(listener);

		// //unsubscribe from any subclasses of the event
		// for (Iterator iter = mListenersMap.keySet().iterator();
		// iter.hasNext();) {
		// Class klass = (Class) iter.next();
		// if (isSubclass(klass,type)) {
		// Set<BusListener> set = mListenersMap.get(klass);
		// set.remove(subscriber);
		// }
		// }

	}

	/**
	 * Publish an event in the event bus. Subscribers that have subscribed to
	 * super classes of the published event will receive notification as well.
	 */
	@SuppressWarnings("unchecked")
	public void publish(EventObject event) {

		// publish to subscribers of the event
		Set<BusListener> listeners = mListenersMap.get(event.getClass());
		publish(listeners, event);

		// publish to subscribers of super classes of the event
		for (Class klass : mListenersMap.keySet()) {
			if (isSubclass(event.getClass(), klass)) {
				Set<BusListener> set = mListenersMap.get(klass);
				publish(set, event);
			}
		}
	}

	private void publish(Set<BusListener> listeners, EventObject event) {
		if (listeners != null) {
			for (BusListener listener : listeners) {
				listener.received(event);
			}
		}

	}

	/**
	 * @return true if class compare is a subclass of class to.
	 */
	@SuppressWarnings("unchecked")
	private boolean isSubclass(Class compare, Class to) {
		if (compare == null || to == null)
			return false;
		for (Class c = compare.getSuperclass(); c != null; c = c
				.getSuperclass()) {
			if (c == to)
				return true;
		}
		return false;
	}

	/**
	 * Get a list of all currently registered listeners.
	 * <p>
	 * Warning - this method is for debug purposes only. Do not use this to
	 * force unsubscription; ensure proper usage of local sub- and unsubscription
	 * instead.
	 * </p>
	 */
	public Map<Class<? extends EventObject>, Set<BusListener>> getRegisteredListeners() {
		System.err
				.println("Warning: you are using a debug only method (EventBus#getRegisteredListeners())");
		return mListenersMap;
	}

}
