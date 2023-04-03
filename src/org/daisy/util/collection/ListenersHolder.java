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
package org.daisy.util.collection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Generic implementation of a Listener holder, that maintains registered
 * listeners as keys in a {@link WeakHashMap} to reduce risks of memory leaks.
 * 
 * @author Romain Deltour
 * 
 */
public class ListenersHolder<E> {
	private Map<E, Object> listeners = Collections
			.synchronizedMap(new WeakHashMap<E, Object>());

	/**
	 * Gets the set of registered listeners. A new set is returned on each call,
	 * so that iteration over the returned is not impacted by further changes to
	 * the listener set.
	 * 
	 * @return
	 */
	public Set<E> getListeners() {
		return new HashSet<E>(listeners.keySet());
	}

	/**
	 * Registers new listeners to this holder.
	 * 
	 * @param listeners new listeners.
	 */
	public void setListeners(Set<E> listeners) {
		if (listeners == null)
			return;
		this.listeners.clear();
		for (E listener : listeners) {
			addListener(listener);
		}
	}

	/**
	 * Registers a new listener to this holder.
	 * 
	 * @param listener
	 *            a new listener.
	 */
	public void addListener(E listener) {
		listeners.put(listener, null);
	}

	/**
	 * Removes the given listener from this holder.
	 * 
	 * @param listener
	 *            a listener
	 */
	public void removeListener(E listener) {
		listeners.remove(listener);
	}
}
