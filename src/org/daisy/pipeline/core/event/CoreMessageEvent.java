package org.daisy.pipeline.core.event;

import javax.xml.stream.Location;

public class CoreMessageEvent extends MessageEvent {

	public CoreMessageEvent(Object source, String message, Type type, Cause cause) {
		this(source, message, type, cause, null);
	}

	public CoreMessageEvent(Object source, String message, Type type) {
		this(source, message, type, MessageEvent.Cause.SYSTEM, null);
	}

	
	public CoreMessageEvent(Object source, String message, Type type, Cause cause, Location location) {
		super(source, message, type, cause, location);
	}

}
