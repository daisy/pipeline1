package org.daisy.pipeline.gui.jobs.runner;

import java.util.List;

import org.daisy.pipeline.gui.model.IJobsFilter;
import org.daisy.pipeline.gui.model.JobInfo;

/**
 * @author Romain Deltour
 * 
 */
public abstract class AbstractJobsRunner implements IJobsRunner {

    private IJobsFilter stateChecker;

    public AbstractJobsRunner() {
        stateChecker = new RunJobsStateChecker();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.jobs.IJobsRunner#run(java.util.List)
     */
    public void run(List<JobInfo> jobInfos) {
        doRun(stateChecker.filter(jobInfos));
    }

    protected abstract void doRun(List<JobInfo> jobInfos);

}
