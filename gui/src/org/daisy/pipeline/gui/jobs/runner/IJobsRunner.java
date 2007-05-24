package org.daisy.pipeline.gui.jobs.runner;

import java.util.List;

import org.daisy.pipeline.gui.model.JobInfo;

/**
 * @author Romain Deltour
 *
 */
public interface IJobsRunner {
    public void run(List<JobInfo> jobInfos);
}
