package org.daisy.pipeline.gui.model;

import java.util.EventListener;

public interface IJobManagerListener extends EventListener {

    public void jobManagerChanged(JobManagerEvent event);
}
