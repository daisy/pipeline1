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
package org.daisy.pipeline.gui.update;

import java.util.zip.ZipEntry;

import org.daisy.pipeline.gui.util.viewers.ZipLabelProvider;

/**
 * Extends the default ZIP label provider by adding the "(ignored)" suffix to
 * ZIP entries ignored by the update operation.
 * 
 * @author Romain Deltour
 * 
 */
public class ZipUpdateLabelProvider extends ZipLabelProvider {

	private ZipUpdateFilter filter;

	/**
	 * Creates a new label provider fetching the ignored status of a ZIP entry
	 * from the given ZIP structure.
	 * 
	 * @param filter
	 *            a ZIP structure filter for update patches.
	 */
	public ZipUpdateLabelProvider(ZipUpdateFilter filter) {
		this.filter = filter;
	}

	@Override
	public String getText(Object element) {
		if ((element instanceof ZipEntry) && (filter != null)
				&& filter.accept((ZipEntry) element)) {
			return super.getText(element) + " (ignored)";
		}
		return super.getText(element);
	}
}
