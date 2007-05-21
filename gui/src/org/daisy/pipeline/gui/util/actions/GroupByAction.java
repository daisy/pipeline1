package org.daisy.pipeline.gui.util.actions;

import org.daisy.pipeline.gui.util.CategorySet;
import org.daisy.pipeline.gui.util.viewers.CategorizedContentProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ContentViewer;

/**
 * @author Romain Deltour
 * 
 */
public class GroupByAction extends Action {

    private ContentViewer viewer;
    private CategorySet categorySet;
    private CategorizedContentProvider contentProvider;
    private boolean wasCheched;

    public GroupByAction(CategorySet categorySet, ContentViewer viewer) {
        super(categorySet.getName(), IAction.AS_RADIO_BUTTON);
        this.viewer = viewer;
        this.contentProvider = (CategorizedContentProvider) viewer
                .getContentProvider();
        this.categorySet = categorySet;
        setChecked(contentProvider.getCategories() == categorySet
                .getCategories());
        this.wasCheched = isChecked();
    }

    @Override
    public void run() {
        if (isChecked() && !wasCheched) {
            contentProvider.setCategories(categorySet.getCategories());
            viewer.refresh();
        }
        wasCheched = isChecked();
    }

}
