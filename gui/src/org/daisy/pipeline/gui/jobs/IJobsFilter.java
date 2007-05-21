package org.daisy.pipeline.gui.jobs;

import java.util.List;

import org.daisy.pipeline.gui.jobs.model.JobInfo;

/**
 * @author Romain Deltour
 *
 */
public interface IJobsFilter {

    public List<JobInfo> filter(List<JobInfo> jobInfos);

}