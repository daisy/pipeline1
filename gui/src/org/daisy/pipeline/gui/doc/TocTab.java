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
package org.daisy.pipeline.gui.doc;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;

import org.daisy.pipeline.gui.util.EFileFilter;
import org.daisy.pipeline.gui.util.HtmlFileFilter;
import org.daisy.pipeline.gui.util.swt.ITabItemProvider;
import org.daisy.pipeline.gui.util.swt.TreeTabItemProvider;
import org.daisy.pipeline.gui.util.viewers.ExpandTreeDoubleClickListener;
import org.daisy.pipeline.gui.util.viewers.FileTreeContentProvider;
import org.daisy.util.file.Directory;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

/**
 * Abstract implementation of a {@link ITocTab} implementing the
 * {@link ITabItemProvider} to provide the SWT widget for the ToC tab.
 * 
 * <p>
 * This implementation represents the ToC as a file hierarchy under the
 * directory {@link #getRootDir()} filtered by a {@link FileFilter}.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public abstract class TocTab extends TreeTabItemProvider implements ITocTab {
	private FileFilter fileFilter;

	/**
	 * Creates the ToC tab given the file filter used returned by
	 * {@link #createFileFilter()}.
	 */
	public TocTab() {
		super();
		fileFilter = createFileFilter();
	}

	public URI getURI() {
		IStructuredSelection sel = (IStructuredSelection) getViewer()
				.getSelection();
		File file = (File) sel.getFirstElement();
		if ((file != null) && file.isFile()) {
			// Note: the file has already been filtered
			return file.toURI();
		}
		return null;
	}

	public boolean select(Object element) {
		// try to get a file object
		File file = null;
		if (element instanceof File) {
			file = (File) element;
		} else {
			file = convertToFile(element);
		}
		if ((file != null) && contains(file)) {
			getViewer().setSelection(
					new StructuredSelection(convertToTocFile(file)), true);
			return true;
		}
		return false;
	}

	/**
	 * Whether the file hierarchy represented by this ToC contains the given
	 * file.
	 * 
	 * @param file
	 *            a file.
	 * @return <code>true</code> if and only if <code>file</code> is
	 *         contained in the file hierarchy represented by this ToC.
	 */
	protected boolean contains(File file) {
		return find(file, new File[] { getRootDir() });
	}

	/**
	 * Tries to adapt the given object to a file represented in this ToC.
	 * 
	 * @param object
	 *            an object.
	 * @return a file represented in this ToC if the given object could be
	 *         adapted or <code>null</code>.
	 */
	protected File convertToFile(Object object) {
		return null;
	}

	/**
	 * Tries to adapt the given object to a file represented in this ToC.
	 * 
	 * @param file
	 *            a file object.
	 * @return a file represented in this ToC if the given object could be
	 *         adapted or <code>null</code>.
	 */
	protected File convertToTocFile(File file) {
		return file;
	}

	@Override
	protected IContentProvider createContentProvider() {
		return new FileTreeContentProvider(fileFilter);
	}

	@Override
	protected Control createControl(TabFolder parent) {
		Control control = super.createControl(parent);
		getViewer().addDoubleClickListener(new ExpandTreeDoubleClickListener());
		return control;
	}

	/**
	 * Creates the file filter used to filter the file hierarchy represented in
	 * this ToC.
	 * 
	 * @return the file filter used to filter the file hierarchy represented in
	 *         this ToC.
	 */
	protected FileFilter createFileFilter() {
		EFileFilter filter = new HtmlFileFilter();
		filter.rejectDir("img"); //$NON-NLS-1$
		return filter;
	}

	@Override
	protected IBaseLabelProvider createLabelProvider() {
		return new DocFileLabelProvider(new TocImageProvider(getRootDir()));
	}

	/**
	 * Utility method to find a file recursively in an array of files.
	 * 
	 * @param file
	 *            the file to find
	 * @param files
	 *            an array of files to search in
	 * @return <code>true</code> if and only <code>file</code> has been
	 *         found in one of <code>files</code>
	 */
	protected boolean find(File file, File[] files) {
		if (file == null) {
			return false;
		}
		for (File tested : files) {
			if (file.equals(tested)) {
				return true;
			}
			if (tested.isDirectory()
					&& find(file, tested.listFiles(fileFilter))) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Object getInput() {
		return getRootDir();
	}

	/**
	 * Returns the root directory of the file hierarchy represented in this ToC.
	 * 
	 * @return the root directory of the file hierarchy represented in this ToC.
	 */
	protected abstract Directory getRootDir();

}
