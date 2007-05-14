package org.daisy.pipeline.gui.jobs;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.pipeline.gui.jobs.model.JobManager;
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
        // Extract the selected jobInfos
        List<JobInfo> jobs = new LinkedList<JobInfo>();
        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
            Object element = iter.next();
            if (element instanceof JobInfo) {
                JobInfo jobInfo = (JobInfo) element;
                if (checkState(jobInfo)) {
                    jobs.add(jobInfo);
                    StateManager.getDefault().scheduled(jobInfo);
                }
            }
        }
        // Sort the selection in the JobManager order
        JobInfo[] jobsArray = jobs.toArray(new JobInfo[0]);
        Arrays.sort(jobsArray, new Comparator<JobInfo>() {
            public int compare(JobInfo o1, JobInfo o2) {
                JobManager jobMan = JobManager.getDefault();
                return jobMan.indexOf(o1) - jobMan.indexOf(o2);
            }
        });
        // Run the selection
        JobsRunner runner = new JobsRunner(jobsArray);
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

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection) selection;
        }
    }

}
