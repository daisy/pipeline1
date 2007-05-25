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
