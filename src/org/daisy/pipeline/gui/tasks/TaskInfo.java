package org.daisy.pipeline.gui.tasks;

import org.daisy.dmfc.core.script.Task;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.pipeline.gui.util.StateObject;

/**
 * @author Romain Deltour
 * 
 */
public class TaskInfo extends StateObject {

    private JobInfo parent;
    private Task task;

    /**
     * @param transInfo
     */
    public TaskInfo(JobInfo parent, Task task) {
        // TODO set description
        super(task.getName(), task.getTransformerInfo().getNiceName());
        this.parent = parent;
        this.task = task;
    }

    public JobInfo getParentJob() {
        return parent;
    }

    public Task getTask() {
        return task;
    }

}
