package org.daisy.dmfc.core.message;

import javax.xml.stream.Location;

import org.daisy.dmfc.core.message.property.Cause;
import org.daisy.dmfc.core.message.property.Type;

/**
 * A message emitted from the Pipeline Core.
 * @author Markus Gylling
 */

public class CoreMessage extends Message {

	/**
	 * Constructor.
	 */
	public CoreMessage(Object originator, String messageText, Type type, Cause cause) {
		super(originator, messageText, type, cause);		
	}

	/**
	 * Constructor.
	 */
	public CoreMessage(Object originator, String messageText, Type type, Cause cause, Location location) {
		super(originator, messageText, type, cause, location);		
	}
}
