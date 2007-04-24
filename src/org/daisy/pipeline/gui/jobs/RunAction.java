package org.daisy.pipeline.gui.jobs;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.daisy.dmfc.core.script.Job;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.pipeline.gui.util.actions.AbstractActionDelegate;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Romain Deltour
 * 
 */
public class RunAction extends AbstractActionDelegate {
    IStructuredSelection selection;

    @Override
    public void run(IAction action) {
        if (selection == null) {
            return;
        }
        // Extract selected jobs
        List<Job> jobs = new LinkedList<Job>();
        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
            Object element = iter.next();
            if (element instanceof JobInfo) {
                JobInfo jobInfo = (JobInfo) element;
                if (checkState(jobInfo)) {
                    jobs.add(jobInfo.getJob());
                }
            }
        }
        JobsRunner runner = new JobsRunner(jobs);
        runner.schedule();
    }

    private boolean checkState(JobInfo jobInfo) {
        //TODO check runnability of failed/aborted jobs
        switch (jobInfo.getSate()) {
        case IDLE:
            return true;
        case RUNNING:
            return false;
        default:
            return false;
        }

    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection) selection;
        }
    }

}
