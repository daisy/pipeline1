/*
 * Created on 2005-mar-07
 */
package org.daisy.dmfc.core;

/**
 * Classes implementing this interface (UIs, loggers, etc) will receive messages
 * from the framework and the transformers.
 * 
 * @author LINUSE
 */
public interface EventListener {
	/**
	 * Receive a message
	 * @param a_message the message
	 */
	public void message(String a_message);
}
