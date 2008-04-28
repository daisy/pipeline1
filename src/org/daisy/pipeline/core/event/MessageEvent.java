/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.core.event;

import javax.xml.stream.Location;

/**
 * Event raised when the system emits a textual message.
 * @author Markus Gylling
 */
public class MessageEvent extends SystemEvent {
	private Type mType;
	private Cause mCause;
	private String mMessage;
	private Location mLocation;

	/**
	 * Minimal constructor. This is the same as calling the default constructor with the cause parameter set to MessageEvent.Cause.SYSTEM
	 * @param source
	 * @param message
	 * @param type
	 */
	public MessageEvent(Object source, String message, Type type) {
		this(source,message,type,MessageEvent.Cause.SYSTEM,null);
	}

	/**
	 * Default constructor.
	 * @param source
	 * @param message
	 * @param type
	 * @param cause
	 */
	public MessageEvent(Object source, String message, Type type, Cause cause) {
		this(source,message,type,cause,null);
	}
	/**
	 * Extended constructor, supporting the optional Location object.
	 * @param source
	 * @param message
	 * @param type
	 * @param cause
	 * @param location
	 */
	public MessageEvent(Object source, String message, Type type, Cause cause, Location location) {
		super(source);
		assert(message!=null);
		assert(type!=null);	
		assert(cause!=null); 		
		mType = type;
		mCause = cause;
		mMessage = message;
		mLocation = location;
	}

	/**
	 * ERROR, WARNING and INFO are message types that are vital to convey to the user.
	 * INFO_FINER is a message type that is not vital to convey (peripheral information).
	 * DEBUG messages are purely developer oriented.
	 */
	public static enum Type {
	    ERROR, WARNING, INFO, INFO_FINER, DEBUG
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
