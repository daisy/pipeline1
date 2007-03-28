package org.daisy.dmfc.core.event;

import org.daisy.dmfc.core.script.Script;

/**
 * An event raised when a {@link org.daisy.dmfc.core.script.Script} object within the Pipeline has started or stopped.
 * @author Markus Gylling
 */
public class ScriptStateChangeEvent extends StateChangeEvent {

	public ScriptStateChangeEvent(Script source, Status state) {
		super(source, state);
	}

	private static final long serialVersionUID = 4477922523293689674L;
	
}