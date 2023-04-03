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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * Traversal listener that hooks {@link SWT#TRAVERSE_TAB_NEXT} and
 * {@link SWT#TRAVERSE_TAB_PREVIOUS} events on a {@link TabFolder} and children
 * controls to cycle through the {@link TabItem}s.
 * 
 * @author Romain Deltour
 * 
 */
public class TabFolderTraverseListener implements TraverseListener {

	/**
	 * Adds a new instance of <code>TabFolderTraverseListener</code> to the
	 * given tab folder and its children.
	 * 
	 * @param folder
	 *            a tab folder
	 */
	public static void addNewTo(TabFolder folder) {
		TraverseListener listener = new TabFolderTraverseListener(folder);
		folder.addTraverseListener(listener);
		for (TabItem item : folder.getItems()) {
			item.getControl().addTraverseListener(listener);
		}
	}

	TabFolder folder;

	/**
	 * Creates a new instance of this listener that will cycle through the tab
	 * items of the given folder.
	 * 
	 * @param folder
	 *            a tab folder
	 */
	public TabFolderTraverseListener(TabFolder folder) {
		super();
		this.folder = folder;
	}

	/**
	 * Hooks {@link SWT#TRAVERSE_TAB_NEXT} and {@link SWT#TRAVERSE_TAB_PREVIOUS}
	 * events on a {@link TabFolder} and children controls to cycle through the
	 * {@link TabItem}s.
	 */
	public void keyTraversed(TraverseEvent e) {
		switch (e.detail) {
		case SWT.TRAVERSE_TAB_NEXT:
			if (e.stateMask == SWT.CONTROL) {
				e.doit = false;
				int sel = folder.getSelectionIndex();
				switchToTab((sel == folder.getItemCount() - 1) ? 0 : sel + 1);
			}
			break;
		case SWT.TRAVERSE_TAB_PREVIOUS:
			if (e.stateMask == (SWT.CONTROL | SWT.SHIFT)) {
				e.doit = false;
				int sel = folder.getSelectionIndex();
				switchToTab((sel == 0) ? folder.getItemCount() - 1 : sel - 1);
			}
			break;
		default:
			e.doit = true;
			break;
		}

	}

	/**
	 * Set the tab folder page selection to the given index and send a
	 * <code>SWT.selection</code> event.
	 * 
	 * @param index
	 *            the index of the new selected tab page
	 */
	private void switchToTab(int index) {
		folder.setSelection(index);
		TabItem item = folder.getItem(index);
		folder.forceFocus();
		// Notify listeners
		Event event = new Event();
		event.item = item;
		folder.notifyListeners(SWT.Selection, event);

	}
}
