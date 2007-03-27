package org.daisy.dmfc.core.event;

import javax.xml.stream.Location;

/**
 * Event raised when the system emits a textual message.
 * @author Markus Gylling
 */
public class SystemMessageEvent extends SystemEvent {
	private Type mType;
	private Cause mCause;
	private String mMessage;
	private Location mLocation;

	public SystemMessageEvent(Object source, String message, Type type) {
		super(source);
		assert(type!=null);
		mType = type;
		mCause = Cause.SYSTEM;
		mMessage = message;
	}
	
	public SystemMessageEvent(Object source, String message, Type type, Cause cause) {
		super(source);
		assert(type!=null);		
		mType = type;
		mCause = cause!=null ? cause : Cause.SYSTEM;
		mMessage = message;
	}
	
	public SystemMessageEvent(Object source, String message, Type type, Cause cause, Location location) {
		super(source);
		assert(type!=null);		
		mType = type;
		mCause = cause!=null ? cause : Cause.SYSTEM;
		mMessage = message;
		mLocation = location;
	}

	public static enum Type {
	    ERROR, WARNING, INFO, DEBUG
	}
	
	public static enum Cause {
	    INPUT, SYSTEM 
	}

	public Cause getCause() {
		return mCause;
	}

	public String getMessage() {
		return mMessage;
	}

	public Type getType() {
		return mType;
	}
	
	public Location getLocation() {
		return mLocation;
	}

	private static final long serialVersionUID = -4372249444244306856L;
	
}
