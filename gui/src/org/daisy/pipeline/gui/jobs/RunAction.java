package org.daisy.pipeline.gui.jobs;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
        // Extract the selected jobInfos
        List<JobInfo> jobInfos = new LinkedList<JobInfo>();
        Iterator iter = selection.iterator();
        while (iter.hasNext()) {
            Object element = iter.next();
            if (element instanceof JobInfo) {
                jobInfos.add((JobInfo) element);
            }
        }
        // Run the selection
        IJobsRunner runner = SequentialJobsRunner.getDefault();
        runner.run(jobInfos);
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            this.selection = (IStructuredSelection) selection;
        }
    }

}
