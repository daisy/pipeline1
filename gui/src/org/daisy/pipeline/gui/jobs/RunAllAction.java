package org.daisy.pipeline.gui.jobs;

import java.util.Arrays;
import java.util.List;

import org.daisy.pipeline.gui.jobs.model.JobInfo;
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
        List<JobInfo> jobs = Arrays.asList(JobManager.getDefault().toArray());
        // Check state and schedule
        for (JobInfo jobInfo : jobs) {
            if (checkState(jobInfo)) {
                StateManager.getDefault().scheduled(jobInfo);
            } else {
                jobs.remove(jobInfo);
            }
        }
        // Run the jobInfos
        JobsRunner runner = new JobsRunner(jobs.toArray(new JobInfo[0]));
        runner.schedule();
    }

    private boolean checkState(JobInfo jobInfo) {
        // TODO check runnability of failed/aborted jobInfos
        switch (jobInfo.getSate()) {
        case IDLE:
            return true;
        case RUNNING:
            return false;
        default:
            return false;
        }

    }

}
