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
package org.daisy.util.jface;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FileTreeContentProvider implements ITreeContentProvider {

	private FileFilter filter;

	public FileTreeContentProvider(FileFilter filter) {
		super();
		this.filter = filter;
	}

	public Object[] getChildren(Object parentElement) {
		if (!(parentElement instanceof File)) {
			throw new IllegalArgumentException("Given element is not a file"); //$NON-NLS-1$
		}
		File file = (File) parentElement;
		return file.listFiles(filter);
	}

	public Object getParent(Object element) {
		if (!(element instanceof File)) {
			throw new IllegalArgumentException("Given element is not a file"); //$NON-NLS-1$
		}
		return ((File) element).getParentFile();
	}

	public boolean hasChildren(Object element) {
		Object[] obj = getChildren(element);
		return obj == null ? false : obj.length > 0;
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		// Nothing
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Nothing
	}

}
