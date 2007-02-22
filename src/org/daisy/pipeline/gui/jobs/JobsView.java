package org.daisy.pipeline.gui.jobs;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.Queue;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

public class JobsView extends ViewPart {
    public static final String ID = "org.daisy.pipeline.gui.views.jobs";

    private TableViewer jobsViewer;

    private Table jobsTable;

    private static final String[] columnNames = { "Status", "Conversion Type",
            "Source", "Destination" };

    private static final int[] columnWidth = { 100, 175, 200, 200 };

    @Override
    public void createPartControl(Composite parent) {
        // TODO remove after the pipeline core is added
        populateFakeQueue();
        // Create the jobs table
        jobsTable = new Table(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.MULTI | SWT.FULL_SELECTION);
        jobsTable.setHeaderVisible(true);
        jobsTable.setLinesVisible(true);
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn column = new TableColumn(jobsTable, SWT.NONE);
            column.setText(columnNames[i]);
            column.setWidth(columnWidth[i]);
            column.setResizable(true);
            column.setMoveable(true);
        }
        // TODO add popup menu to jobs table
        // jobsTable.setMenu(createPopUpMenu());

        // Configure the jobs table viewer
        jobsViewer = new TableViewer(jobsTable);
        jobsViewer.setContentProvider(new JobsContentProvider());
        jobsViewer.setLabelProvider(new JobsLabelProvider());
        jobsViewer.setInput(Queue.getInstance());
        getSite().setSelectionProvider(jobsViewer);
    }

    private void populateFakeQueue() {
        Queue cue = Queue.getInstance();
        Job job;
        for (int i = 0; i < 10; i++) {
            job = new Job(
                    new File(System.getProperty("user.dir"), "source"+i+".src"),
                    new File(System.getProperty("user.dir"), "dest"+i+".dst"), 1,
                    new ScriptHandler());
            cue.addJobToQueue(job);
        }
    }

    /**
     * Passes the focus request to the viewer's control.
     */
    @Override
    public void setFocus() {
        jobsViewer.getControl().setFocus();
    }
}