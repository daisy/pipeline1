package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.jobs.JobsView;
import org.daisy.pipeline.gui.messages.MessagesView;
import org.daisy.pipeline.gui.progress.JobProgressView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class JobsPerspective implements IPerspectiveFactory {

    public static final String ID = "org.daisy.pipeline.gui.perspectives.jobs"; //$NON-NLS-1$
    public static final String DETAILS_FODER_ID = ID + "detailsFolder"; //$NON-NLS-1$

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        layout.addView(JobsView.ID, IPageLayout.LEFT, 1.0f, editorArea);
        layout.addView(MessagesView.ID, IPageLayout.BOTTOM, 0.7f, JobsView.ID);
        IFolderLayout folder = layout.createFolder(DETAILS_FODER_ID,
                IPageLayout.RIGHT, 0.5f, JobsView.ID);
        // folder.addView(ParametersView.ID);
        folder.addView(JobProgressView.ID);

        PerspectiveUtil.addCommonShortcuts(layout);
    }

}
