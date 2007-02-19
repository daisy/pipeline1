package org.daisy.pipeline.gui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class JobsPerspective implements IPerspectiveFactory {
    
    public static final String ID = "org.daisy.pipeline.gui.perspectives.jobs";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);
		
		layout.addStandaloneView(JobsView.ID,  false, IPageLayout.LEFT, 1.0f, editorArea);
    }

}
