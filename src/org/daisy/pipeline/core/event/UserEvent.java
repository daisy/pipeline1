package org.daisy.pipeline.core.event;

/**
 * Base class for all Pipeline events raised by the user of the system.
 * @see org.daisy.pipeline.core.event.SystemEvent
 * @author Markus Gylling
 */

public class UserEvent extends Event {

	public UserEvent(Object source) {
		super(source);
	}

	private static final long serialVersionUID = 907421233396027398L;
}