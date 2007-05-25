/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.jobs.runner;

import java.util.ArrayList;
import java.util.List;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.jobs.JobsLabelProvider;
import org.daisy.pipeline.gui.jobs.Messages;
import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.JobsStateGroupFilter;
import org.daisy.pipeline.gui.model.StateManager;
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
    private static final String alwaysRunAbortedKey = "alwaysRunFinishedKey"; //$NON-NLS-1$
    private static final String alwaysRunFailedKey = "alwaysRunFinishedKey"; //$NON-NLS-1$
    private static final String alwaysRunFinishedKey = "alwaysRunFinishedKey"; //$NON-NLS-1$

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
                Messages.dialog_abortedJobs_title,
                Messages.dialog_abortedJobs_message,
                MessageDialog.WARNING, Messages.dialog_abortedJobs_toggle,
                alwaysRunAbortedKey);
    }

    @Override
    protected List<JobInfo> filterFailed(List<JobInfo> jobInfos) {
        return filterListWithDialog(
                jobInfos,
                Messages.dialog_failedJobs_title,
                Messages.dialog_failedJobs_message,
                MessageDialog.WARNING, Messages.dialog_failedJobs_toggle,
                alwaysRunFailedKey);
    }

    @Override
    protected List<JobInfo> filterFinished(List<JobInfo> jobInfos) {
        return filterListWithDialog(
                jobInfos,
                Messages.dialog_finishedJobs_title,
                Messages.dialog_finishedJobs_message,
                MessageDialog.WARNING, Messages.dialog_finishedJobs_toggle,
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
            dialog.setTitle(Messages.dialog_scheduledJobs_title);
            dialog
                    .setMessage(Messages.dialog_scheduledJobs_message);
            dialog.setInput(jobInfos);
            dialog.setContentProvider(new ArrayContentProvider());
            dialog.setLabelProvider(new JobsLabelProvider());
            dialog.open();
        }
        return empyJobList;
    }

}
