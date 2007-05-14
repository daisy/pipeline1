package org.daisy.pipeline.gui.jobs;

import org.daisy.dmfc.exception.JobAbortedException;
import org.daisy.dmfc.exception.JobFailedException;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Romain Deltour
 * 
 */
public class JobsRunner extends Job {
    JobInfo[] jobInfos;
    JobInfo currJobInfo;

    public JobsRunner(JobInfo[] jobInfos) {
        super("Pipeline Jobs Runner");
        this.jobInfos = jobInfos;
        setUser(true);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Run Pipeline Jobs", jobInfos.length);
        try {
            for (JobInfo jobInfo : jobInfos) {
                try {
                    currJobInfo = jobInfo;
                    monitor.subTask("Running " + jobInfo.getName());
                    GuiPlugin.get().getCore().execute(jobInfo.getJob());
                    monitor.worked(1);
                } catch (JobFailedException e) {
                    if (e instanceof JobAbortedException) {
                        StateManager.getDefault().aborted(jobInfo);
                    } else {
                        StateManager.getDefault().failed(jobInfo);
                        GuiPlugin.get().error(e.getLocalizedMessage(), e);
                    }
                } finally {
                    currJobInfo = null;
                }
            }
        } finally {
            monitor.done();
        }
        return Status.OK_STATUS;
    }
    
    public JobInfo currentJobInfo(){
        return currJobInfo;
    }
}
