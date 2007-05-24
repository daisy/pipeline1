package org.daisy.pipeline.gui.model;

import java.util.List;


/**
 * @author Romain Deltour
 * 
 */
public interface IJobChangeListener {
    public void jobChanged(JobInfo jobInfo);

    public void jobsChanged(List<JobInfo> jobInfos);
}
