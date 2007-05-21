package org.daisy.pipeline.gui.jobs;

import java.util.List;

import org.daisy.pipeline.gui.jobs.model.JobInfo;

/**
 * @author Romain Deltour
 *
 */
public interface IJobsRunner {
    public void run(List<JobInfo> jobInfos);
}
