package org.daisy.util.fileset.manipulation.manipulators;

import org.daisy.util.xml.stax.ContextStack;

public interface XMLEventValueConsumer {
	/**
	 * @param value the original String value of the exposed node
	 * @param context the context stack 
	 * @return a String or null 
	 */
	public String nextValue(String value, ContextStack context);
	
}
