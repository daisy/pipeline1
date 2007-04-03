package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.doc.DocPerspective;
import org.daisy.pipeline.gui.doc.DocView;
import org.daisy.pipeline.gui.jobs.wizard.NewJobWizard;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;

/**
 * @author Romain Deltour
 * 
 */
public final class PerspectiveHelper {
    private PerspectiveHelper() {
    }

    public static void addCommonShortcuts(IPageLayout layout) {
        // Perspective shortcuts
        layout.addPerspectiveShortcut(JobsPerspective.ID);
        layout.addPerspectiveShortcut(DocPerspective.ID);
        // View shortcuts
        layout.addShowViewShortcut(LogView.ID);
        layout.addShowViewShortcut(DocView.ID);
        layout.addNewWizardShortcut(NewJobWizard.ID);
    }

    public static void configurePerspectiveBar() {
        // TODO use product configuration file instead
        PlatformUI.getPreferenceStore().setValue(
                IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR,
                IWorkbenchPreferenceConstants.TOP_RIGHT);
        PlatformUI.getPreferenceStore().setValue(
                IWorkbenchPreferenceConstants.SHOW_TEXT_ON_PERSPECTIVE_BAR,
                false);
        PlatformUI.getPreferenceStore().setValue(
                IWorkbenchPreferenceConstants.PERSPECTIVE_BAR_EXTRAS,
                JobsPerspective.ID + "," + DocPerspective.ID);

    }
}
