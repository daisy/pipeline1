package org.daisy.pipeline.gui.jobs.model;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Task;
import org.daisy.pipeline.gui.tasks.TaskInfo;
import org.daisy.pipeline.gui.util.StateObject;

/**
 * @author Romain Deltour
 * 
 */
public class JobInfo extends StateObject{
    private Job job;
    private List<TaskInfo> tasks;

    public JobInfo(Job job) {
        //TODO check job is not null
        super(job.getScript().getNicename(),"");
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

    private void createTaskInfos() {
        for (Task task : job.getScript().getTasks()) {
            tasks.add(new TaskInfo(task));
        }
    }
}
