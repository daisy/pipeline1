/*
 * Created on 2005-mar-07
 */
package org.daisy.dmfc.core;

/**
 * A user interface implementing this interface will get requests 
 * of user input from Transformers (provided the Transformers are
 * in interactive mode).
 * 
 * @author LINUSE
 */
public interface InputListener {
	
	/**
	 * Get (interactive) input from the user.
	 * @param a_prompt
	 * @return the input from the user
	 */
	public String getInputAsString(Prompt a_prompt);
	
}
