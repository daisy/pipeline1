package org.daisy.pipeline.gui.util.actions;

import java.util.List;

import org.daisy.pipeline.gui.util.Category;
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
    private List<Category> categories;
    private CategorizedContentProvider contentProvider;
    private boolean wasCheched;

    public GroupByAction(String name, ContentViewer viewer,
            List<Category> categories) {
        super(name, IAction.AS_RADIO_BUTTON);
        this.viewer = viewer;
        this.contentProvider = (CategorizedContentProvider) viewer
                .getContentProvider();
        this.categories = categories;
        setChecked(contentProvider.getCategories() == categories);
        this.wasCheched = isChecked();
    }

    @Override
    public void run() {
        if (isChecked() && !wasCheched) {
            contentProvider.setCategories(categories);
            viewer.refresh();
        }
        wasCheched = isChecked();
    }

}
