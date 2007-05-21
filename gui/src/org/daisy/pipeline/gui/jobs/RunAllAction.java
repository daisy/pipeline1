package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.jobs.model.JobManager;
import org.daisy.pipeline.gui.util.actions.AbstractActionDelegate;
import org.eclipse.jface.action.IAction;

/**
 * @author Romain Deltour
 * 
 */
public class RunAllAction extends AbstractActionDelegate {

    @Override
    public void run(IAction action) {
        // Run the jobInfos
        IJobsRunner runner = SequentialJobsRunner.getDefault();
        runner.run(JobManager.getDefault().toList());
    }

}
