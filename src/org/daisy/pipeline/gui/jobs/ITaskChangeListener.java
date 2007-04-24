package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.tasks.TaskInfo;

/**
 * @author Romain Deltour
 *
 */
public interface ITaskChangeListener {
    public void taskChanged(TaskInfo task);
}
