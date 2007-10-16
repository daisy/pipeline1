package org.daisy.pipeline.core.transformer;

import javax.xml.stream.Location;

import org.daisy.pipeline.core.event.MessageEvent;

/**
 * An interface that a Transformer can implement in order to allow
 * delegates limited access to the messaging and i18n framework 
 * @author Markus Gylling
 */
public interface TransformerDelegateListener {

	/**
	 * Emit a progress measure between 0 and 1.
	 */
	public void delegateProgress(Object delegate, double progress);
	
	/**
	 * Emit a message.
	 */
	public void delegateMessage(Object delegate, String message, MessageEvent.Type type, MessageEvent.Cause cause, Location location);
	
	/**
	 * Request localization of a string through accessing the Transformers message bundles.
	 */
	public String delegateLocalize(String message, Object param);
		
}
