/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * @author Romain Deltour
 * 
 */
public class JobsStateGroupFilter implements IJobsFilter {
    protected boolean checkScheduled = false;
    protected boolean checkFinished = false;
    protected boolean checkFailed = false;
    protected boolean checkAborted = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.jobs.IJobsFilter#checkStates(java.util.List)
     */
    public List<JobInfo> filter(List<JobInfo> jobInfos) {
        if (!checkAborted && !checkFailed && !checkFinished && !checkScheduled) {
            return jobInfos;
        }
        // Sort jobs into different categories
        List<JobInfo> scheduledJobs = new LinkedList<JobInfo>();
        List<JobInfo> finishedJobs = new LinkedList<JobInfo>();
        List<JobInfo> failedJobs = new LinkedList<JobInfo>();
        List<JobInfo> idleJobs = new LinkedList<JobInfo>();
        List<JobInfo> abortedJobs = new LinkedList<JobInfo>();
        Iterator<JobInfo> iter = jobInfos.iterator();
        while (iter.hasNext()) {
            JobInfo jobInfo = iter.next();
            switch (jobInfo.getSate()) {
            case ABORTED:
                abortedJobs.add(jobInfo);
                break;
            case FAILED:
                failedJobs.add(jobInfo);
                break;
            case FINISHED:
                finishedJobs.add(jobInfo);
                break;
            case IDLE:
                idleJobs.add(jobInfo);
                break;
            case RUNNING:
                scheduledJobs.add(jobInfo);
                break;
            case WAITING:
                scheduledJobs.add(jobInfo);
                break;
            default:
                // IDLE jobs don't need to be checked
                break;
            }
        }
        // check each list
        List<JobInfo> jobsToRun = new LinkedList<JobInfo>();
        jobsToRun.addAll(filterAborted(abortedJobs));
        jobsToRun.addAll(filterFailed(failedJobs));
        jobsToRun.addAll(filterFinished(finishedJobs));
        jobsToRun.addAll(filterIdle(idleJobs));
        jobsToRun.addAll(filterScheduled(scheduledJobs));
        return jobsToRun;

    }

    protected List<JobInfo> filterAborted(List<JobInfo> jobInfos) {
        return jobInfos;
    }

    protected List<JobInfo> filterFailed(List<JobInfo> jobInfos) {
        return jobInfos;
    }

    protected List<JobInfo> filterFinished(List<JobInfo> jobInfos) {
        return jobInfos;
    }

    protected List<JobInfo> filterIdle(List<JobInfo> jobInfos) {
        return jobInfos;
    }

    protected List<JobInfo> filterScheduled(List<JobInfo> jobInfos) {
        return jobInfos;
    }

}