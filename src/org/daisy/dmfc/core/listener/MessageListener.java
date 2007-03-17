package org.daisy.dmfc.core.listener;

import java.util.EventListener;

import org.daisy.dmfc.core.message.Message;

/**
 * An implementor of this interface will recieve messages 
 * emitted by the Pipeline core framework, or by a Transformer.
 * <p>All messages are subclasses of {@link org.daisy.dmfc.core.message.Message}.</p>
 * @author Markus Gylling
 * @since 1.5
 */
public interface MessageListener extends EventListener {

  /**
   * Recieve a message emitted from the Pipeline core 
   * framework, or from a transformer.
   */	
  public void message(Message message);	
  
}
