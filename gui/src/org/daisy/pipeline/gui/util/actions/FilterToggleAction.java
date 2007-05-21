package org.daisy.pipeline.gui.util.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Romain Deltour
 * 
 */
public class FilterToggleAction extends Action {

    private StructuredViewer viewer;
    private ViewerFilter filter;
    private boolean checked;

    public FilterToggleAction(String name, ViewerFilter filter,
            StructuredViewer viewer) {
        super(name, IAction.AS_CHECK_BOX);
        this.viewer = viewer;
        this.filter = filter;
        this.checked = false;
        initCheckedState();
    }

    @Override
    public void run() {
        checked = !checked;
        if (checked) {
            viewer.addFilter(filter);
        } else {
            viewer.removeFilter(filter);
        }
        setChecked(checked);
    }

    private void initCheckedState() {
        ViewerFilter[] filters = viewer.getFilters();
        for (int i = 0; i < filters.length && !checked; i++) {
            checked = (filters[i] != null && filters[i].equals(filter));
        }
        setChecked(checked);
    }

}
