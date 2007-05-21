package org.daisy.pipeline.gui.jobs;

import java.util.List;

import org.daisy.pipeline.gui.jobs.model.JobInfo;

/**
 * @author Romain Deltour
 * 
 */
public interface IJobChangeListener {
    public void jobChanged(JobInfo jobInfo);

    public void jobsChanged(List<JobInfo> jobInfos);
}
