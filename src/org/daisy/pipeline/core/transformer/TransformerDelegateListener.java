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
package org.daisy.pipeline.core.transformer;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.event.MessageEvent;

/**
 * An interface that a Transformer can implement in order to allow
 * delegates limited access to the event/messaging and i18n framework 
 * @author Markus Gylling
 */
public interface TransformerDelegateListener {

	/**
	 * Report a delegate progress measure (between 0 and 1) to the listener.
	 */
	public void delegateProgress(Object delegate, double progress);
	
	/**
	 * Emit a delegate message to the listener.
	 */
	public void delegateMessage(Object delegate, String message, MessageEvent.Type type, MessageEvent.Cause cause, Location location);
	
	/**
	 * Request localization through accessing the listeners message bundles.
	 */
	public String delegateLocalize(String key, Object[] params);
	
	/**
	 * Query the listener on whether it has recieved an abort event.
	 * @return true if an abort event has been recieved, else false.
	 */
	public boolean delegateCheckAbort();
		
}
