package org.daisy.dmfc.core.event;

/**
 * Event raised when a user requests a system abort.
 * @author Markus Gylling
 */
public class UserAbortEvent extends UserEvent {

	public UserAbortEvent(Object source) {
		super(source);
	}

	private static final long serialVersionUID = -7838520080823692507L;
}
