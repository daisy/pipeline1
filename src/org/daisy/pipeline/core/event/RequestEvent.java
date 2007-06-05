package org.daisy.pipeline.core.event;

/**
 * Event raised when the system requests input from the user of the system during exection time (followed by a user reply event).
 * @see {@link org.daisy.pipeline.core.event.UserReplyEvent}
 * @author Markus Gylling
 */
public class RequestEvent extends SystemEvent {
	
	private String mRequest;

	public RequestEvent(Object source, String request) {
		super(source);
		mRequest = request;
	}

	public String getRequest() {
		return mRequest;
	}
	
	private static final long serialVersionUID = 5302901987693469283L;

}
