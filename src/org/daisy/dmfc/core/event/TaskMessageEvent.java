package org.daisy.dmfc.core.event;

import javax.xml.stream.Location;

import org.daisy.dmfc.core.script.Task;

/**
 * Event raised when a Transformer emits a textual message.
 * @author Markus Gylling
 */
public class TaskMessageEvent extends MessageEvent {

	public TaskMessageEvent(Task source, String message, Type type, Cause cause, Location location) {
		super(source, message, type, cause, location);
	}

}
