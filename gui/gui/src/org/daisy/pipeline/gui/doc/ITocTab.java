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
package org.daisy.pipeline.gui.doc;

import java.net.URI;

import org.daisy.pipeline.gui.util.swt.ITabItemProvider;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * The generic facade to a Table of Content tab in the {@link DocView}.
 * 
 * @author Romain Deltour
 * 
 */
public interface ITocTab extends ITabItemProvider {

	/**
	 * Returns the URI of the doc currently selected in this ToC tab.
	 * 
	 * @return the URI of the doc currently selected in this ToC tab.
	 */
	public URI getURI();

	/**
	 * Returns the JFace tree viewer internally used to represent the ToC in
	 * this tab.
	 * 
	 * @return the tree viewer internally used to represent the ToC in this tab.
	 */
	public TreeViewer getViewer();

	/**
	 * Selects the given object in this ToC tab.
	 * 
	 * @param element
	 *            the element of this ToC to select.
	 * @return <code>true</code> if and only if the ToC in this tab contains
	 *         the given element and it was successfully selected.
	 */
	public boolean select(Object element);
}
