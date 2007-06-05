package org.daisy.pipeline.core.event;

import org.daisy.pipeline.core.script.Task;

/**
 * An event raised when a {@link org.daisy.pipeline.core.script.Task} object within the Pipeline has started or stopped.
 * @author Markus Gylling
 */
public class TaskStateChangeEvent extends StateChangeEvent {

	public TaskStateChangeEvent(Task source, Status state) {
		super(source, state);
	}

	private static final long serialVersionUID = 3683114276276735596L;
	
}
