package org.daisy.pipeline.gui.jobs;

import java.util.EnumSet;

import org.daisy.pipeline.gui.model.IJobsFilter;
import org.daisy.pipeline.gui.model.JobManager;
import org.daisy.pipeline.gui.model.JobStateFilter;
import org.daisy.pipeline.gui.model.StateManager;
import org.daisy.pipeline.gui.util.actions.AbstractActionDelegate;
import org.daisy.util.execution.State;
import org.eclipse.jface.action.IAction;

/**
 * @author Romain Deltour
 * 
 */
public class CancelAllAction extends AbstractActionDelegate {
    private IJobsFilter stateFilter = new JobStateFilter(EnumSet.of(
            State.RUNNING, State.WAITING));

    @Override
    public void run(IAction action) {
        // Run the jobInfos
        StateManager.getDefault().cancel(
                stateFilter.filter(JobManager.getDefault().toList()));
    }
}
