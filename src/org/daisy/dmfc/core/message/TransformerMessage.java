package org.daisy.dmfc.core.message;

import javax.xml.stream.Location;

import org.daisy.dmfc.core.message.property.Cause;
import org.daisy.dmfc.core.message.property.Type;
import org.daisy.dmfc.core.transformer.Transformer;

/**
 * A message emitted from a Pipeline Transformer
 * @author Markus Gylling
 */

public class TransformerMessage extends Message {

	/**
	 * Constructor.
	 */
	public TransformerMessage(Transformer originator, String messageText, Type type, Cause cause) {
		super(originator,messageText, type, cause);
	}

	/**
	 * Constructor.
	 */
	public TransformerMessage(Transformer originator, String messageText, Type type, Cause cause, Location location) {
		super(originator,messageText, type, cause, location);
	}
	
	public Transformer getSource() {
		return (Transformer)super.source;
	}
	
}
