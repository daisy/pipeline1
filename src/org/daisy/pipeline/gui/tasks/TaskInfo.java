package org.daisy.pipeline.gui.tasks;

import org.daisy.dmfc.core.script.Task;
import org.daisy.pipeline.gui.util.StateObject;

/**
 * @author Romain Deltour
 * 
 */
public class TaskInfo extends StateObject {

    /**
     * @param transInfo
     */
    public TaskInfo(Task task) {
        // TODO set description
        super(task.getName(), "description");
    }

}
