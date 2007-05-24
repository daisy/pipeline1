package org.daisy.pipeline.gui.model;

import org.daisy.dmfc.core.event.MessageEvent;

/**
 * @author Romain Deltour
 * 
 */
public class JobMessageEvent extends MessageEvent {
    private static final long serialVersionUID = -2270775828710227738L;
    private JobInfo jobInfo;

    public JobMessageEvent(MessageEvent message, JobInfo jobInfo) {
        super(message.getSource(), message.getMessage(), message.getType(),
                message.getCause(), message.getLocation());
        this.jobInfo = jobInfo;
    }

    public JobInfo getJobInfo() {
        return jobInfo;
    }
}
