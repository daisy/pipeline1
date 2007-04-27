package org.daisy.pipeline.gui;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
            IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    @Override
    public String getInitialWindowPerspectiveId() {
        return JobsPerspective.ID;
    }

    @Override
    public void initialize(IWorkbenchConfigurer configurer) {
        configurer.setSaveAndRestore(true);
        configurer.declareImage(ISharedImages.IMG_TOOL_UNDO, GuiPlugin
                .getIcon(IIconsKeys.EDIT_UNDO), true);
        configurer.declareImage(ISharedImages.IMG_TOOL_REDO, GuiPlugin
                .getIcon(IIconsKeys.EDIT_REDO), true);
        configurer.declareImage(ISharedImages.IMG_TOOL_DELETE,
                GuiPlugin.getIcon(IIconsKeys.EDIT_DELETE), true);

        PlatformUI.getPreferenceStore().setValue(
                IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS,
                false);
    }
}
