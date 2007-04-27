package org.daisy.pipeline.gui.jobs;

import org.daisy.dmfc.core.script.Job;
import org.daisy.pipeline.gui.jobs.model.JobManager;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Romain Deltour
 * 
 */
public class NewJobOperation extends AbstractOperation {

    private Job job;

    public NewJobOperation(Job job) {
        super("Create Job");
        this.job = job;
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        return redo(monitor, info);
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        JobManager.getDefault().add(job);
        return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        JobManager.getDefault().remove(job);
        return Status.OK_STATUS;
    }

}
