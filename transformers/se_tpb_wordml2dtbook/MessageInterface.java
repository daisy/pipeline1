package se_tpb_wordml2dtbook;

import org.daisy.pipeline.core.event.MessageEvent;

/**
 * 
 * Emit a message.
 * 
 * @author  Joel Hakansson, TPB
 * @version 2007 apr 11
 * @since 1.0
 */
public interface MessageInterface {

	/**
	 * Emit a message.
	 *  
	 * @param type
	 * @param idstr
	 * @param params
	 */
	public void sendMessage(MessageEvent.Type type, String idstr, Object[] params);
}
