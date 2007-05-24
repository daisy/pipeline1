package org.daisy.pipeline.gui.jobs.runner;

import org.daisy.dmfc.exception.JobAbortedException;
import org.daisy.dmfc.exception.JobFailedException;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.jobs.Messages;
import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.StateManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

/**
 * @author Romain Deltour
 * 
 */
public class JobRunnerJob extends Job {

    public static final Object FAMILY = new Object();
    private JobInfo jobInfo;
    private Object subfamily;

    public JobRunnerJob(JobInfo jobInfo) {
        this(jobInfo, null);
    }

    public JobRunnerJob(JobInfo jobInfo, Object subfamily) {
        super(NLS.bind(Messages.runnerJob_name, jobInfo.getName()));
        this.jobInfo = jobInfo;
        this.subfamily = subfamily;
        setUser(true);
    }

    @Override
    public boolean belongsTo(Object family) {
        return family == FAMILY || family == subfamily;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(Messages.runnerJob_message, IProgressMonitor.UNKNOWN);
        try {
            GuiPlugin.get().getCore().execute(jobInfo.getJob());
        } catch (JobFailedException e) {
            if (e instanceof JobAbortedException) {
                StateManager.getDefault().aborted(jobInfo);
            } else {
                StateManager.getDefault().failed(jobInfo);
                GuiPlugin.get().error(e.getLocalizedMessage(), e);
            }
        } finally {
            monitor.done();
        }
        return Status.OK_STATUS;
    }

    /**
     * @return
     */
    public JobInfo getJobInfo() {
        return jobInfo;
    }

}
