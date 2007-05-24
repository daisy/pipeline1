package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.doc.DocView;
import org.daisy.pipeline.gui.jobs.JobsView;
import org.daisy.pipeline.gui.jobs.wizard.NewJobWizard;
import org.daisy.pipeline.gui.messages.MessagesView;
import org.daisy.pipeline.gui.progress.JobProgressView;
import org.eclipse.ui.IPageLayout;

/**
 * @author Romain Deltour
 * 
 */
public final class PerspectiveUtil {
    private PerspectiveUtil() {
    }

    public static void addCommonShortcuts(IPageLayout layout) {
        // Perspective shortcuts
        layout.addPerspectiveShortcut(JobsPerspective.ID);
        layout.addPerspectiveShortcut(DocPerspective.ID);
        // View shortcuts
        layout.addShowViewShortcut(DocView.ID);
        layout.addShowViewShortcut(JobsView.ID);
        layout.addShowViewShortcut(MessagesView.ID);
        // layout.addShowViewShortcut(ParametersView.ID);
        layout.addShowViewShortcut(JobProgressView.ID);
        layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView"); //$NON-NLS-1$
        // Wizard shortcuts
        layout.addNewWizardShortcut(NewJobWizard.ID);
    }
}
