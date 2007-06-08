/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.gui.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author Romain Deltour
 * 
 */
public class TabFolderTraverseListener implements TraverseListener {

    TabFolder folder;

    public TabFolderTraverseListener(TabFolder folder) {
        super();
        this.folder = folder;
    }

    public static void addNewTo(TabFolder folder) {
        TraverseListener listener = new TabFolderTraverseListener(folder);
        folder.addTraverseListener(listener);
        for (TabItem item : folder.getItems()) {
            item.getControl().addTraverseListener(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.events.TraverseListener#keyTraversed(org.eclipse.swt.events.TraverseEvent)
     */
    public void keyTraversed(TraverseEvent e) {
        switch (e.detail) {
        case SWT.TRAVERSE_TAB_NEXT:
            if (e.stateMask == SWT.CONTROL) {
                int sel = folder.getSelectionIndex();
                int next = (sel == folder.getItemCount() - 1) ? 0 : sel + 1;
                TabItem item = folder.getItem(next);
                folder.setSelection(item);
                item.getControl().setFocus();
            } else {
                e.doit = true;
            }
            break;
        case SWT.TRAVERSE_TAB_PREVIOUS:
            if (e.stateMask == (SWT.CONTROL | SWT.SHIFT)) {
                int sel = folder.getSelectionIndex();
                int prev = (sel == 0) ? folder.getItemCount() - 1 : sel - 1;
                TabItem item = folder.getItem(prev);
                folder.setSelection(item);
                item.getControl().setFocus();
            } else {
                e.doit = true;
            }
        default:
            e.doit = true;
            break;
        }

    }

}
