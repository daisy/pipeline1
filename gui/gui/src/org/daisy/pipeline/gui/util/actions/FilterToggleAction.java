/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
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
