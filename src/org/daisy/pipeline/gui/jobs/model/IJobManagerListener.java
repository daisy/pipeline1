package org.daisy.pipeline.gui.jobs.model;

import java.util.EventListener;

public interface IJobManagerListener extends EventListener {

    public void jobManagerChanged(JobManagerEvent event);
}
