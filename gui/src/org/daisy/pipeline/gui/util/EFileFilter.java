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
package org.daisy.pipeline.gui.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.util.file.Directory;
import org.daisy.util.file.EFile;

/**
 * A file filter designed to be use with {@link EFile}s and {@link Directory}s.
 * 
 * @author Romain Deltour
 * 
 */
public abstract class EFileFilter implements FileFilter {

	private static final String SVN_DIR = ".svn"; //$NON-NLS-1$
	private static final String DS_STORE = ".DS_Store"; //$NON-NLS-1$
	/** A set of automatically filtered directory names */
	private Set<String> filteredDirNames;
	/** A set of automatically filtered file names */
	private Set<String> filteredFileNames;

	/**
	 * Constructs the filter and adds default values to the names filtered by
	 * default (e.g. .DS_Store, .svn)
	 */
	public EFileFilter() {
		super();
		filteredDirNames = new HashSet<String>();
		filteredDirNames.add(SVN_DIR);
		filteredFileNames = new HashSet<String>();
		filteredFileNames.add(DS_STORE);
	}

	/**
	 * Tests whether or not the specified abstract pathname should be included
	 * in a pathname list.
	 * <p>
	 * The <code>EFileFilter</code> implementation of this interface
	 * dispatches to the {@link #acceptEFile(EFile)} and
	 * {@link #acceptDir(Directory)} methods.
	 * </p>
	 * 
	 * @param file
	 *            The abstract pathname to be tested
	 * @return <code>true</code> if and only if <code>pathname</code> should
	 *         be included
	 */
	public boolean accept(File file) {

		if (file.isDirectory()) {
			try {
				return acceptDir(new Directory(file));
			} catch (IOException e) {
				GuiPlugin.get().error(
						"Couldn't create Directory from file " + file, e); //$NON-NLS-1$
				return false;
			}
		} else {
			return acceptEFile(new EFile(file));
		}
	}

	/**
	 * Tests whether or not the specified abstract file pathname should be
	 * included in a pathname list.
	 * <p>
	 * This default implementation filters out ".DS_Store" files.
	 * </p>
	 * 
	 * @param file
	 *            The abstract pathname to be tested
	 * @return <code>true</code> if and only if <code>pathname</code> should
	 *         be included
	 */
	protected boolean acceptEFile(EFile file) {
		return !filteredFileNames.contains(file.getName());
	}

	/**
	 * Tests whether or not the specified abstract directory pathname should be
	 * included in a pathname list.
	 * <p>
	 * This default implementation filters out ".svn" directories.
	 * </p>
	 * 
	 * @param dir
	 *            The abstract pathname to be tested
	 * @return <code>true</code> if and only if <code>pathname</code> should
	 *         be included
	 */
	protected boolean acceptDir(Directory dir) {
		return !filteredDirNames.contains(dir.getName());
	}

	/**
	 * Configures this filter to reject the directories of the given name.
	 * 
	 * @param name
	 *            the name of directories to filter out
	 */
	public void rejectDir(String name) {
		filteredDirNames.add(name);
	}

}
