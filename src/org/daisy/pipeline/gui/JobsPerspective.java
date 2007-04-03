package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.jobs.JobDetailsView;
import org.daisy.pipeline.gui.jobs.JobsView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class JobsPerspective implements IPerspectiveFactory {

    public static final String ID = "org.daisy.pipeline.gui.perspectives.jobs";

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        layout.setFixed(false);

        // layout.setFixed(true);
        layout.addStandaloneView(JobsView.ID, true, IPageLayout.LEFT, 1.0f,
                editorArea);
        // layout.setFixed(false);
        layout.addView(JobDetailsView.ID, IPageLayout.RIGHT, 0.8f, JobsView.ID);

        PerspectiveHelper.addCommonShortcuts(layout);
    }

}
