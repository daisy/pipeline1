package org.daisy.pipeline.gui.jobs;

import org.daisy.dmfc.exception.JobAbortedException;
import org.daisy.dmfc.exception.JobFailedException;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Romain Deltour
 * 
 */
public class JobsRunner extends org.eclipse.core.runtime.jobs.Job {
    JobInfo[] jobs;

    public JobsRunner(JobInfo[] jobs) {
        super("Pipeline Jobs Runner");
        this.jobs = jobs;
        setUser(true);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Run Pipeline Jobs", jobs.length);
        for (JobInfo jobInfo : jobs) {
            try {
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
            }
        }
        monitor.done();
        return Status.OK_STATUS;
    }
}
