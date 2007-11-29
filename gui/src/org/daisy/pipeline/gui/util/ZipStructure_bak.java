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
package org.daisy.pipeline.gui.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * A wrapper for a {@link ZipFile} to allow an easy access to its hierarchical
 * structure.
 * 
 * @author Romain Deltour
 * 
 */
public class ZipStructure_bak {

	/** The wrapped ZIP file */
	private ZipFile zipFile;
	/** The root of the ZIP file */
	private ZipEntry root;
	/** A cache of the children of every entry in the ZIP file */
	private Map<ZipEntry, List<ZipEntry>> children;
	/** A cache of every directory in the ZIP file */
	private Map<IPath, ZipEntry> directories;
	/** The number of entries in the ZIP file */
	private int entryCount;
	/** A filter on the ZIP structure */
	private Filter<ZipEntry> filter;

	/**
	 * Creates a new <code>ZipStructure</code> for the given ZIP file.
	 * 
	 * @param zipFile
	 *            the ZIP file wrapped by the new <code>ZipStructure</code>.
	 */
	public ZipStructure_bak(ZipFile zipFile) {
		this(zipFile, null);
	}

	/**
	 * Creates a new <code>ZipStructure</code> for the given ZIP file and
	 * filtered by the given ZIP entry filter.
	 * 
	 * @param zipFile
	 *            the ZIP file wrapped by the new <code>ZipStructure</code>.
	 * @param filter
	 *            a ZIP entry filter for this ZIP structure
	 */
	public ZipStructure_bak(ZipFile zipFile, Filter<ZipEntry> filter) {
		this.zipFile = zipFile;
		this.root = new ZipEntry("/");//$NON-NLS-1$
		this.children = new HashMap<ZipEntry, List<ZipEntry>>(1000);
		this.directories = new HashMap<IPath, ZipEntry>(1000);
		this.entryCount = 0;
		this.filter = filter;
		initCache();
	}

	/**
	 * Creates and cache a new directory zip entry with the specified name, if
	 * and only if it has not already been created. If the parent of the given
	 * element does not already exist it will be recursively created as well.
	 * 
	 * @param pathname
	 *            The path representing the zip directory entry
	 * @return The element represented by this pathname (it may have already
	 *         existed)
	 */
	private ZipEntry cacheDir(IPath pathname) {
		ZipEntry existingEntry = directories.get(pathname);
		if (existingEntry != null) {
			return existingEntry;
		}

		ZipEntry parent;
		if (pathname.segmentCount() == 1) {
			parent = root;
		} else {
			parent = cacheDir(pathname.removeLastSegments(1));
		}
		ZipEntry newEntry = new ZipEntry(pathname.toString());
		directories.put(pathname, newEntry);
		List<ZipEntry> childList = new ArrayList<ZipEntry>();
		children.put(newEntry, childList);

		List<ZipEntry> parentChildList = children.get(parent);
		parentChildList.add(newEntry);
		return newEntry;
	}

	/**
	 * Cache the given file zip entry in the children map.
	 */
	private void cacheFile(ZipEntry entry) {
		IPath pathname = new Path(entry.getName());
		ZipEntry parent;
		if (pathname.segmentCount() == 1) {
			parent = root;
		} else {
			parent = directories.get(pathname.removeLastSegments(1));
		}

		List<ZipEntry> childList = children.get(parent);
		childList.add(entry);
	}

	/**
	 * Returns the list of children of the given ZIP entry.
	 * 
	 * @param parent
	 *            a ZIP entry.
	 * @return the list of children of the given ZIP entry (can be empty but not
	 *         <code>null</code>).
	 */
	public List<ZipEntry> getChildren(ZipEntry parent) {
		if (parent.isDirectory()) {
			return children.get(parent);
		} else {
			return new ArrayList<ZipEntry>(0);
		}
	}

	/**
	 * Returns the number of entries in this ZIP structure.
	 * 
	 * @return the number of entries in this ZIP structure.
	 */
	public int getEntryCount() {
		return entryCount;
	}

	/**
	 * Returns the parent of the given ZIP entry.
	 * 
	 * @param entry
	 *            a ZIP entry.
	 * @return the parent of the given ZIP entry or <code>null</code> if the
	 *         given entry is the root.
	 */
	public ZipEntry getParent(ZipEntry entry) {
		IPath path = new Path(entry.getName());
		ZipEntry parent;
		if (path.segmentCount() == 0) {
			parent = null;
		} else if (path.segmentCount() == 1) {
			parent = root;
		} else {
			parent = directories.get(path.removeLastSegments(1));
		}
		return parent;
	}

	/**
	 * Returns the root entry.
	 * 
	 * @return the root entry.
	 */
	public ZipEntry getRoot() {
		return root;
	}

	/**
	 * Returns the underlying ZIP file.
	 * 
	 * @return the underlying ZIP file.
	 */
	public ZipFile getZipFile() {
		return zipFile;
	}

	private void initCache() {
		children.put(root, new ArrayList<ZipEntry>());
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			entryCount++;
			ZipEntry entry = entries.nextElement();
			IPath path = new Path(entry.getName()).addTrailingSeparator();

			if (entry.isDirectory()) {
				cacheDir(path);
			} else {
				// Ensure the container structure for all levels above this is
				// initialized
				// Once we hit a higher-level container that's already added we
				// need go no further
				int pathSegmentCount = path.segmentCount();
				if (pathSegmentCount > 1) {
					cacheDir(path.uptoSegment(pathSegmentCount - 1));
				}
				cacheFile(entry);
			}
		}
	}
}
