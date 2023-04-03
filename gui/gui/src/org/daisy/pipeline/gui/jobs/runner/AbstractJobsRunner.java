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
