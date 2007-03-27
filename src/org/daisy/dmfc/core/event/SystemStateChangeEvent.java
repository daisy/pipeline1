package org.daisy.dmfc.core.event;

/**
 * An event raised when an object within the Pipeline has started or stopped.
 * @author Markus Gylling
 */
public class SystemStateChangeEvent extends SystemEvent {

	private Status mStatus;
	
	public SystemStateChangeEvent(Object source, Status status) {
		super(source);
		mStatus = status;
	}

	public static enum Status {
		STARTING, STARTED, STOPPING, STOPPED
	}
	
	public Status getState() {
		return mStatus;
	}

	private static final long serialVersionUID = -4672109252993604750L;
	
}
