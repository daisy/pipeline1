package org.daisy.pipeline.gui.doc;

import org.daisy.pipeline.gui.PerspectiveHelper;
import org.daisy.pipeline.gui.scripts.ScriptsView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class DocPerspective implements IPerspectiveFactory {

    public static final String ID = "org.daisy.pipeline.gui.perspectives.doc";

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        layout.setFixed(false);

        layout.addView(ScriptsView.ID, IPageLayout.LEFT, 1.0f, editorArea);
        layout.addView(DocView.ID, IPageLayout.RIGHT, 0.3f, ScriptsView.ID);

        PerspectiveHelper.addCommonShortcuts(layout);
    }

}
