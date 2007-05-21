package org.daisy.pipeline.gui.messages;

import org.daisy.dmfc.core.event.MessageEvent;

/**
 * @author Romain Deltour
 * 
 */
public interface IMessageManagerListener {
    public void messageAdded(MessageEvent message);
}
