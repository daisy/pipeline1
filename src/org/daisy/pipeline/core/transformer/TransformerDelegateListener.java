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
