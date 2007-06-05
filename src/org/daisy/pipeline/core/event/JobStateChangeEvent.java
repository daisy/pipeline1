package org.daisy.pipeline.core.event;

import org.daisy.pipeline.core.script.Job;

/**
 * An event raised when a {@link org.daisy.pipeline.core.script.Job} object
 * within the Pipeline has started or stopped.
 * 
 * @author Markus Gylling
 */
public class JobStateChangeEvent extends StateChangeEvent {

    public JobStateChangeEvent(Job source, Status state) {
        super(source, state);
    }

    private static final long serialVersionUID = 4477922523293689674L;

}