package org.daisy.pipeline.gui.jobs;

import java.util.List;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.exception.ScriptAbortException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Romain Deltour
 * 
 */
public class JobsRunner extends org.eclipse.core.runtime.jobs.Job {
    List<Job> jobs;

    public JobsRunner(List<Job> jobs) {
        super("run job");
        this.jobs = jobs;
        setUser(true);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Run Pipeline Jobs", jobs.size());
        for (Job job : jobs) {
            try {
                monitor.subTask("Running " + job.getScript().getNicename());
                PipelineGuiPlugin.getDefault().getCore().execute(job);
                monitor.worked(1);
            } catch (ScriptException e) {
                if (e instanceof ScriptAbortException) {
                    StateManager.getInstance().aborted(job);
                }else {
                    
                }
                // TODO check aborted/failed status
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        monitor.done();
        return Status.OK_STATUS;
    }
}
