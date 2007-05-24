package org.daisy.pipeline.gui.model;

import java.util.List;


/**
 * @author Romain Deltour
 *
 */
public interface IJobsFilter {

    public List<JobInfo> filter(List<JobInfo> jobInfos);

}