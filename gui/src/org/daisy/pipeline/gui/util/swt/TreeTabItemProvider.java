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

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

/**
 * An abstract implementation of a {@link ITabItemProvider} that uses a
 * {@link TreeViewer} for client area of the provided tab item.
 * 
 * @author Romain Deltour
 * 
 */
public abstract class TreeTabItemProvider extends DefaultTabItemProvider {

	private TreeViewer treeViewer;

	@Override
	protected Control createControl(TabFolder parent) {
		treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.setContentProvider(createContentProvider());
		treeViewer.setLabelProvider(createLabelProvider());
		treeViewer.setInput(getInput());
		return treeViewer.getControl();
	}

	/**
	 * Returns the tree viewer used in the client area of the provided tab item.
	 * 
	 * @return the tree viewer used in the client area of the provided tab item.
	 */
	public TreeViewer getViewer() {
		return treeViewer;
	}

	@Override
	protected abstract String getTitle();

	/**
	 * Creates the content provider for underlying tree viewer of this tab item.
	 * 
	 * @return the content provider for underlying tree viewer of this tab item.
	 */
	protected abstract IContentProvider createContentProvider();

	/**
	 * Returns the input used by the underlying tree viewer of this tab item.
	 * 
	 * @return the input used by the underlying tree viewer of this tab item.
	 */
	protected abstract Object getInput();

	/**
	 * Returns the label provider used by the underlying tree viewer of this tab
	 * item.
	 * 
	 * @return the label provider used by the underlying tree viewer of this tab
	 *         item.
	 */
	protected abstract IBaseLabelProvider createLabelProvider();

}