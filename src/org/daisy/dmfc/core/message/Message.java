package org.daisy.dmfc.core.message;

import java.util.EventObject;

import javax.xml.stream.Location;

import org.daisy.dmfc.core.message.property.Cause;
import org.daisy.dmfc.core.message.property.Type;

/**
 * An abstract base for all messages created and emitted by the 
 * Pipeline core framework or by transformers.
 * @author Markus Gylling
 */
public abstract class Message extends EventObject {

	private String messageText = null;
	private Type type = null;
	private Cause cause = null;
	private Location location = null;
	
	/**
	 * Default constructor.
	 */
	protected Message(Object originator, String messageText, Type type, Cause cause) {	  
	  super(originator);	
	  this.messageText = messageText;
	  this.type = type;
	  this.cause = cause;
	}

	/**
	 * Extended constructor.
	 */
	protected Message(Object originator, String messageText, Type type, Cause cause, Location location) {
	  super(originator);	
	  this.messageText = messageText;
	  this.type = type;
	  this.cause = cause;
	  this.location = location;
	}

	/**
	 * Retrieve the textual message associated with this message.
	 * This message should have been adapted to the current Locale by the emitter.   
	 */	
	public String getText() {
		return this.messageText;		
	}

	/**
	 * Retrieve the Type object associated with this message.   
	 */	
	public Type getType() {
		return this.type;		
	}

	/**
	 * Retrieve the Cause object associated with this message.   
	 */	
	public Cause getCause() {
		return this.cause;		
	}
	
	/**
	 * Retrieve the {@link javax.xml.stream.Location} object associated with this message, 
	 * or null if the message contains no Location.   
	 */
	public Location getLocation() {
		return this.location;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.EventObject#toString()
	 */
	public String toString() {
		//TODO specialize
		return super.toString();
	}

}
