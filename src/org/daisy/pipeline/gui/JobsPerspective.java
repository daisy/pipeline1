package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.jobs.JobDetailsView;
import org.daisy.pipeline.gui.jobs.JobsView;
import org.daisy.pipeline.gui.messages.MessagesView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class JobsPerspective implements IPerspectiveFactory {

    public static final String ID = "org.daisy.pipeline.gui.perspectives.jobs";

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        // layout.setFixed(true);
        layout.addView(JobsView.ID, IPageLayout.LEFT, 1.0f, editorArea);
        // layout.setFixed(false);
        layout.addView(MessagesView.ID, IPageLayout.BOTTOM, 0.7f, JobsView.ID);
        layout.addView(JobDetailsView.ID, IPageLayout.RIGHT, 0.7f, JobsView.ID);

        PerspectiveHelper.addCommonShortcuts(layout);
    }

}
