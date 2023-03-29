/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.gui.model;

import java.util.ArrayList;
import java.util.List;

import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Task;
import org.daisy.pipeline.gui.jobs.runner.JobRunnerJob;
import org.daisy.pipeline.gui.util.StateTracker;
import org.daisy.util.execution.State;

/**
 * @author Romain Deltour
 * 
 */
public class JobInfo extends StateTracker {
    private Job job;
    private JobRunnerJob runnerJob;
    private List<TaskInfo> tasks;

    public JobInfo(Job job) {
        this(null, job);
    }

    public JobInfo(String name, Job job) {
        super(name != null ? name : job.getScript().getNicename());
        this.job = job;
        this.tasks = new ArrayList<TaskInfo>(job.getScript().getTasks().size());
        createTaskInfos();
    }

    public Job getJob() {
        return job;
    }

    public JobRunnerJob getRunnerJob() {
        return runnerJob;
    }

    public List<TaskInfo> getTasks() {
        return tasks;
    }

    public int getTaskNumber() {
        return tasks.size();
    }

    public void setRunnerJob(JobRunnerJob runnerJob) {
        this.runnerJob = runnerJob;
    }

    private void createTaskInfos() {
        for (Task task : job.getScript().getTasks()) {
            tasks.add(new TaskInfo(this, task));
        }
    }

    @Override
    protected void setIdle() {
        super.setIdle();
        for (TaskInfo task : tasks) {
            task.setState(State.IDLE);
        }
    }

    @Override
    protected void setWaiting() {
        super.setWaiting();
        for (TaskInfo task : tasks) {
            task.setState(State.WAITING);
        }
    }

    @Override
    protected void stoppedRunning() {
        super.stoppedRunning();
        runnerJob = null;
    }

}
