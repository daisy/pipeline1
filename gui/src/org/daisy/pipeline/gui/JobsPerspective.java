package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.jobs.JobDetailsView;
import org.daisy.pipeline.gui.jobs.JobsView;
import org.daisy.pipeline.gui.messages.MessagesView;
import org.daisy.pipeline.gui.parameters.ParametersView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class JobsPerspective implements IPerspectiveFactory {

    public static final String ID = "org.daisy.pipeline.gui.perspectives.jobs";
    public static final String DETAILS_FODER_ID = ID + "detailsFolder";

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        // layout.setFixed(true);
        layout.addView(JobsView.ID, IPageLayout.LEFT, 1.0f, editorArea);
        // layout.setFixed(false);
        layout.addView(MessagesView.ID, IPageLayout.BOTTOM, 0.7f, JobsView.ID);
        IFolderLayout folder = layout.createFolder(DETAILS_FODER_ID,
                IPageLayout.RIGHT, 0.5f, JobsView.ID);
        folder.addView(ParametersView.ID);
        folder.addView(JobDetailsView.ID);

        PerspectiveHelper.addCommonShortcuts(layout);
    }

}