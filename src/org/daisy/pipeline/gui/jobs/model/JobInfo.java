package org.daisy.pipeline.gui.jobs.model;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Task;
import org.daisy.pipeline.gui.tasks.TaskInfo;
import org.daisy.pipeline.gui.util.StateTracker;
import org.daisy.util.execution.State;

/**
 * @author Romain Deltour
 * 
 */
public class JobInfo extends StateTracker {
    private Job job;
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

    public List<TaskInfo> getTasks() {
        return tasks;
    }

    @Override
    public synchronized void setState(State state) {
        super.setState(state);
        if (state == State.WAITING) {
            for (TaskInfo task : tasks) {
                task.setState(State.WAITING);
            }
        }
    }

    private void createTaskInfos() {
        for (Task task : job.getScript().getTasks()) {
            tasks.add(new TaskInfo(this, task));
        }
    }
}
