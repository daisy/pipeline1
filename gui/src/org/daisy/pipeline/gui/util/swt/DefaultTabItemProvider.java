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
package org.daisy.pipeline.gui.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author Romain Deltour
 * 
 */
public abstract class DefaultTabItemProvider implements ITabItemProvider {

    private TabItem item;

    public DefaultTabItemProvider() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.doc.ITabITemProvider#createTabItem(org.eclipse.swt.widgets.TabFolder)
     */
    public TabItem createTabItem(TabFolder parent) {
        item = new TabItem(parent, SWT.NONE);
        item.setText(getTitle());
        item.setToolTipText(getToolTipText());
        item.setControl(createControl(parent));
        return item;
    }

    protected abstract Control createControl(TabFolder parent);

    public TabItem getTabItem() {
        return item;
    }

    protected abstract String getTitle();

    protected abstract String getToolTipText();

}