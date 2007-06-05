package org.daisy.pipeline.core.event;

/**
 * An event raised when an object within the Pipeline has started or stopped.
 * @author Markus Gylling
 */
public class StateChangeEvent extends SystemEvent {

	private Status mState;
	
	public StateChangeEvent(Object source, Status state) {
		super(source);
		mState = state;
	}

	public static enum Status {
		//STARTING, STARTED, STOPPING, STOPPED
		STARTED, STOPPED
	}
	
	public Status getState() {
		return mState;
	}

	private static final long serialVersionUID = -4672109252993604750L;
	
}
