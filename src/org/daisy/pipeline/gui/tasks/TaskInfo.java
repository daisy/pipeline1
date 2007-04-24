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

    /**
     * @param transInfo
     */
    public TaskInfo(JobInfo parent, Task task) {
        // TODO set description
        super(task.getName(), "description");
        this.parent = parent;
    }

    public JobInfo getParentJob() {
        return parent;
    }

}
