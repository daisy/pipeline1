package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.jobs.model.JobInfo;

/**
 * @author Romain Deltour
 * 
 */
public interface IJobChangeListener {
    public void jobChanged(JobInfo job);
}
