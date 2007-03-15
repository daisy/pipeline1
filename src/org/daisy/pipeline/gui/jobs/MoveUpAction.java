package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.daisy.pipeline.gui.jobs.model.Job;
import org.daisy.pipeline.gui.jobs.model.JobManager;
import org.daisy.pipeline.gui.util.DefaultSelectionEnabler;
import org.daisy.pipeline.gui.util.ISelectionEnabler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;

public class MoveUpAction extends AbstractJobAction {

    public MoveUpAction(IViewPart view) {
        super(view, "Move Up", PipelineGuiPlugin.getIcon(IIconsKeys.GO_UP));
    }

    @Override
    protected ISelectionEnabler getEnabler() {
        return new DefaultSelectionEnabler(ISelectionEnabler.Mode.SINGLE,
                new Class[] { Job.class }) {

            @Override
            protected boolean checkContent(IStructuredSelection selection) {
                Job job = (Job) selection.getFirstElement();
                return JobManager.getInstance().indexOf(job) > 0;
            }

        };
    }

    @Override
    protected IUndoableOperation getOperation() {
        return new AbstractOperation(getText()) {
            private Job job = MoveUpAction.this.selectedJobs.get(0);
            private JobManager jobManager = JobManager.getInstance();

            @Override
            public IStatus execute(IProgressMonitor monitor, IAdaptable info)
                    throws ExecutionException {
                redo(monitor, info);
                return Status.OK_STATUS;
            }

            @Override
            public IStatus redo(IProgressMonitor monitor, IAdaptable info)
                    throws ExecutionException {
                jobManager.moveUp(job);
                if (jobManager.indexOf(job) == 0) {
                    MoveUpAction.this.setEnabled(false);
                }
                return Status.OK_STATUS;
            }

            @Override
            public IStatus undo(IProgressMonitor monitor, IAdaptable info)
                    throws ExecutionException {
                jobManager.moveDown(job);
                return Status.OK_STATUS;
            }

        };
    }

}
