package org.daisy.dmfc.core.event;

/**
 * Base class for all Pipeline events raised by the system.
 * @see {@link org.daisy.dmfc.core.event.UserEvent}
 * @author Markus Gylling
 */
public class SystemEvent extends Event {

	public SystemEvent(Object source) {
		super(source);
	}

	private static final long serialVersionUID = -7391745019400791872L;
	
}
