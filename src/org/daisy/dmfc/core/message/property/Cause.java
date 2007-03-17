package org.daisy.dmfc.core.message.property;

/**
 * The Cause property of a Message is an indicator on the categorical belongings
 * of the sender of the message.
 * This enum can be used by the listener to sort and filter Messages. 
 * @author Markus Gylling
 */
public class Cause {
	private Cause(){}

	/**
	 * The INPUT Cause property on a Message indicates that the message 
	 * concerns a user provided object (such as an input file or a request 
	 * or a parameter).
	 */
	public static final Cause INPUT = new Cause(); 
	
	/**
	 * The SYSTEM Cause property on a Message indicates that the message 
	 * concerns a non-user provided object (such as a system or runtime event 
	 * over which the user has no direct governance).
	 */	
	public static final Cause SYSTEM = new Cause();
	 
}