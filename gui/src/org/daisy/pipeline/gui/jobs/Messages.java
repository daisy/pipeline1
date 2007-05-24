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
    public static String runnerJob_message;
    public static String runnerJob_name;
    public static String uiJob_updateJobs;
    public static String uiJob_updateJobs_subtask;
    public static String uiJob_updateJobs_task;
}
