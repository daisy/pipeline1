package org.daisy.dmfc.core.event;

import javax.xml.stream.Location;

/**
 * Event raised when a Transformer emits a textual message.
 * @author Markus Gylling
 */
public class TransformerMessageEvent extends MessageEvent {

	public TransformerMessageEvent(Object source, String message, Type type, Cause cause, Location location) {
		super(source, message, type, cause, location);
	}

}
