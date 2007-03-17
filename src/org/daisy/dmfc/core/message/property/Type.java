package org.daisy.dmfc.core.message.property;

/**
 * The Type property of a Message is an indicator on its nature.
 * This enum can be used by the reciever to sort and filter Messages. 
 * @author Markus Gylling
 */
public class Type {
	private Type(){}
	
	public static final Type ERROR = new Type();
	public static final Type WARNING = new Type(); 
	public static final Type INFO = new Type(); 
	public static final Type DEBUG = new Type(); 
	 
}