package org.daisy.pipeline.gui.jobs;

import java.util.ArrayList;
import java.util.List;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.pipeline.gui.util.ListSelectionMessageToggleDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Romain Deltour
 * 
 */
public class RunJobsStateChecker extends JobsStateGroupFilter {

    private Shell shell;
    private static final List<JobInfo> empyJobList = new ArrayList<JobInfo>(0);
    private static final String alwaysRunAbortedKey = "alwaysRunFinishedKey";
    private static final String alwaysRunFailedKey = "alwaysRunFinishedKey";
    private static final String alwaysRunFinishedKey = "alwaysRunFinishedKey";

    public RunJobsStateChecker() {
        this(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        checkAborted = true;
        checkFailed = true;
        checkFinished = true;
        checkScheduled = true;
    }

    public RunJobsStateChecker(Shell shell) {
        this.shell = shell;
    }

    private List<JobInfo> filterListWithDialog(List<JobInfo> jobInfos,
            String title, String message, int dialogType, String toggleMessage,
            String prefKey) {
        List<JobInfo> res = jobInfos;
        IPreferenceStore prefStore = GuiPlugin.get().getPreferenceStore();
        boolean alwaysRun = prefStore.getString(prefKey).equals(
                MessageDialogWithToggle.ALWAYS);
        if (!alwaysRun && jobInfos != null && jobInfos.size() > 0) {
            ListSelectionMessageToggleDialog dialog = new ListSelectionMessageToggleDialog(
                    shell, title, null, message, dialogType, toggleMessage,
                    false, jobInfos, new ArrayContentProvider(),
                    new JobsLabelProvider());
            dialog.setPrefKey(prefKey);
            dialog.setPrefStore(prefStore);
            dialog.setInitialElementSelections(jobInfos);
            if (dialog.open() == IDialogConstants.OK_ID) {
                Object[] selection = dialog.getResult();
                res = new ArrayList<JobInfo>(selection.length);
                for (Object object : selection) {
                    res.add((JobInfo) object);
                }
            } else {
                res = empyJobList;
            }
        }
        // Reset jobs
        StateManager.getDefault().reset(res);
        return res;
    }

    @Override
    protected List<JobInfo> filterAborted(List<JobInfo> jobInfos) {
        return filterListWithDialog(
                jobInfos,
                "Aborted Jobs",
                "Please select the jobs you want to re-execute.\n\n"
                        + "Note: The following jobs have been aborted during their previous execution, re-executing them might fail.",
                MessageDialog.WARNING, "Always re-execute finished jobs",
                alwaysRunAbortedKey);
    }

    @Override
    protected List<JobInfo> filterFailed(List<JobInfo> jobInfos) {
        return filterListWithDialog(
                jobInfos,
                "Failed Jobs",
                "Please select the jobs you want to re-execute.\n\n"
                        + "Note: The following jobs have previously been executed and they failed, re-executing them will probably fail again.",
                MessageDialog.WARNING, "Always re-execute failed jobs",
                alwaysRunFailedKey);
    }

    @Override
    protected List<JobInfo> filterFinished(List<JobInfo> jobInfos) {
        return filterListWithDialog(
                jobInfos,
                "Finished Jobs",
                "Please select the jobs you want to re-execute.\n\n"
                        + "Note: The following jobs have already been executed, if you re-execute them the progress information on the previous execution will be lost.",
                MessageDialog.WARNING, "Always re-execute finished jobs",
                alwaysRunFinishedKey);
    }

    @Override
    protected List<JobInfo> filterIdle(List<JobInfo> jobInfos) {
        // Idle jobs don't need to be checked
        return super.filterIdle(jobInfos);
    }

    @Override
    protected List<JobInfo> filterScheduled(List<JobInfo> jobInfos) {
        if (jobInfos != null && jobInfos.size() > 0) {
            ListDialog dialog = new ListDialog(shell);
            dialog.setTitle("Scheduled Jobs");
            dialog
                    .setMessage("The operation will not affect the following already scheduled jobs.");
            dialog.setInput(jobInfos);
            dialog.setContentProvider(new ArrayContentProvider());
            dialog.setLabelProvider(new JobsLabelProvider());
            dialog.open();
        }
        return empyJobList;
    }

}
