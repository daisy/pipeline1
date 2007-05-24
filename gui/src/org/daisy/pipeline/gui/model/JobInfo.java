package org.daisy.pipeline.gui.model;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Task;
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
