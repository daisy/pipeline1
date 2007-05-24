package org.daisy.pipeline.gui.model;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.daisy.util.execution.State;

/**
 * @author Romain Deltour
 * 
 */
public class JobStateFilter implements IJobsFilter {

    private EnumSet<State> acceptedSates;

    public JobStateFilter(EnumSet<State> acceptedSates) {
        this.acceptedSates = acceptedSates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.jobs.IJobsFilter#filter(java.util.List)
     */
    public List<JobInfo> filter(List<JobInfo> jobInfos) {
        Iterator<JobInfo> iter = jobInfos.iterator();
        while (iter.hasNext()) {
            JobInfo jobInfo = (JobInfo) iter.next();
            if (!acceptedSates.contains(jobInfo.getSate())) {
                iter.remove();
            }
        }
        return jobInfos;
    }

}
