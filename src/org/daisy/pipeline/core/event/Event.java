package org.daisy.pipeline.core.event;

import java.util.EventObject;

/**
 * Base class for all events occuring within the Pipeline.
 * @author Markus Gylling
 */
public class Event extends EventObject {

	public Event(Object source) {
		super(source);
	}

	private static final long serialVersionUID = -8457722024536827644L;
}
