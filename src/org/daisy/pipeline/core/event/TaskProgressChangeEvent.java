package org.daisy.pipeline.core.event;

import org.daisy.pipeline.core.script.Task;

/**
 * An event raised when a Transformer within the Pipeline changes its progress.
 * @author Markus Gylling
 */
public class TaskProgressChangeEvent extends ProgressChangeEvent {

	public TaskProgressChangeEvent(Task source, double progress) {
		super(source, progress);
	}

}
