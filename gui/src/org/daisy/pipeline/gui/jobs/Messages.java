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
package org.daisy.pipeline.gui.jobs;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.daisy.pipeline.gui.jobs.messages"; //$NON-NLS-1$

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String action_delete;
    public static String action_moveDown;
    public static String action_moveToBottom;
    public static String action_moveToTop;
    public static String action_moveUp;
    public static String dialog_abortedJobs_message;
    public static String dialog_abortedJobs_title;
    public static String dialog_abortedJobs_toggle;
    public static String dialog_failedJobs_message;
    public static String dialog_failedJobs_title;
    public static String dialog_failedJobs_toggle;
    public static String dialog_finishedJobs_message;
    public static String dialog_finishedJobs_title;
    public static String dialog_finishedJobs_toggle;
    public static String dialog_scheduledJobs_message;
    public static String dialog_scheduledJobs_title;
    public static String error_delete_message;
    public static String error_delete_title;
    public static String heading_jobs;
    public static String heading_status;
    public static String operation_newJob;
    public static String prefPage_run_aborted_label;
    public static String prefPage_run_always;
    public static String prefPage_run_failed_label;
    public static String prefPage_run_finished_label;
    public static String prefPage_run_prompt;
    public static String runnerJob_message;
    public static String runnerJob_name;
    public static String uiJob_updateJobs;
    public static String uiJob_updateJobs_subtask;
    public static String uiJob_updateJobs_task;
}
