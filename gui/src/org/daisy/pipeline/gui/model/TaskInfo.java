package org.daisy.pipeline.gui.model;

import org.daisy.dmfc.core.script.Task;
import org.daisy.pipeline.gui.util.StateTracker;

/**
 * @author Romain Deltour
 * 
 */
public class TaskInfo extends StateTracker {

    private JobInfo parent;
    private Task task;

    /**
     * @param transInfo
     */
    public TaskInfo(JobInfo parent, Task task) {
        super(task.getTransformerInfo().getNiceName());
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
