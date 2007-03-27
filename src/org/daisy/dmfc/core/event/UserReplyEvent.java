package org.daisy.dmfc.core.event;

/**
 * Event raised when a user provides input to the system during exection time (preceeded by a system prompt event).
 * @see {@link org.daisy.dmfc.core.event.SystemRequestEvent}
 * @author Markus Gylling
 */
public class UserReplyEvent extends UserEvent {

	private String mUserInput = null;

	public UserReplyEvent(Object source, String reply) {
		super(source);
		mUserInput  = reply;
	}

	public String getReply() {
		return mUserInput;
	}
	
	private static final long serialVersionUID = 7458715035224831016L;

}
