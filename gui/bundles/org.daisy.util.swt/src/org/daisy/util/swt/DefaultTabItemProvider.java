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
package org.daisy.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * An abstract default implementation of a <code>ITabItemProvider</code>.
 * 
 * @author Romain Deltour
 * 
 */
public abstract class DefaultTabItemProvider implements ITabItemProvider {

	private TabItem item;

	/**
	 * Creates this provider.
	 */
	public DefaultTabItemProvider() {
		super();
	}

	/**
	 * Creates the <code>TabItem</code> from the
	 * {@link #createControl(TabFolder)} method, with the title returned by
	 * {@link #getTitle()} and the tooltip from {@link #getToolTipText()}.
	 */
	public TabItem createTabItem(TabFolder parent) {
		item = new TabItem(parent, SWT.NONE);
		item.setText(getTitle());
		item.setToolTipText(getToolTipText());
		item.setControl(createControl(parent));
		return item;
	}

	/**
	 * Creates the control used in the client area of the provided tab item.
	 * 
	 * @param parent
	 *            The parent tab folder
	 * @return the control used in the client area of the provided tab item.
	 */
	protected abstract Control createControl(TabFolder parent);

	public TabItem getTabItem() {
		return item;
	}

	/**
	 * Returns the title of the provided tab item.
	 * 
	 * @return the title of the provided tab item.
	 */
	protected abstract String getTitle();

	/**
	 * Returns the tooltip text for the provided tab item.
	 * 
	 * @return the tooltip text for the provided tab item.
	 */
	protected abstract String getToolTipText();

}