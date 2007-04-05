package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.tasks.TaskListContentProvider;
import org.daisy.pipeline.gui.tasks.TaskListViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class JobDetailsView extends ViewPart {

    public static final String ID = "org.daisy.pipeline.gui.views.jobDetails";

    private StructuredViewer viewer;

    public JobDetailsView() {

    }

    @Override
    public void createPartControl(Composite parent) {
        viewer = new TaskListViewer(parent, SWT.SINGLE);
        viewer.setContentProvider(new TaskListContentProvider());
        // TODO viewer.setInput(selected job);
        getSite().setSelectionProvider(viewer);
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

}
