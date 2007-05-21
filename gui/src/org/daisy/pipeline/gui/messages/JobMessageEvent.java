package org.daisy.pipeline.gui.messages;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.jobs.model.JobInfo;

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
