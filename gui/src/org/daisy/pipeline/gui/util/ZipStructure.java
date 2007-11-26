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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
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
public class ZipStructure {

	private class ZipEntryInfo {
		ZipEntry zipEntry;
		ZipEntry parent;
		List<ZipEntry> children;

		/**
		 * Creates a new entry info for the given ZIP entry.
		 * 
		 * @param zipEntry
		 *            a ZIP entry
		 */
		protected ZipEntryInfo(ZipEntry zipEntry) {
			this.zipEntry = zipEntry;
			children = new LinkedList<ZipEntry>();
		}
	}

	/** The wrapped ZIP file */
	private ZipFile zipFile;
	private Map<String, ZipEntryInfo> entryInfos;
	/** The root of the ZIP file */
	private ZipEntry root;
	/** A filter on the ZIP structure */
	private Filter<ZipEntry> filter;

	/**
	 * Creates a new <code>ZipStructure</code> for the given ZIP file.
	 * 
	 * @param zipFile
	 *            the ZIP file wrapped by the new <code>ZipStructure</code>.
	 */
	public ZipStructure(ZipFile zipFile) {
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
	public ZipStructure(ZipFile zipFile, Filter<ZipEntry> filter) {
		this.zipFile = zipFile;
		this.root = new ZipEntry("/");//$NON-NLS-1$
		this.entryInfos = new HashMap<String, ZipEntryInfo>(1000);
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
	 */
	private ZipEntryInfo cacheDir(IPath pathname) {
		ZipEntryInfo existingInfo = entryInfos.get(pathname.toString());
		if (existingInfo != null) {
			return existingInfo;
		}
		ZipEntry newEntry = new ZipEntry(pathname.toString());
		if ((filter != null) && !filter.accept(newEntry)) {
			return null;
		}
		ZipEntryInfo parentInfo;
		if (pathname.segmentCount() == 1) {
			parentInfo = entryInfos.get(root.getName());
		} else {
			parentInfo = cacheDir(pathname.removeLastSegments(1));
		}
		if (parentInfo == null) {// was filtered out
			return null;
		}
		ZipEntryInfo newInfo = new ZipEntryInfo(newEntry);
		newInfo.parent = parentInfo.zipEntry;
		entryInfos.put(pathname.toString(), newInfo);
		parentInfo.children.add(newEntry);
		return newInfo;
	}

	/**
	 * Cache the given file zip entry in the children map.
	 */
	private void cacheFile(ZipEntry entry) {
		if ((filter != null) && !filter.accept(entry)) {
			return;
		}
		ZipEntryInfo info = new ZipEntryInfo(entry);
		ZipEntryInfo parentInfo;
		IPath pathname = new Path(entry.getName());
		if (pathname.segmentCount() == 1) {
			parentInfo = entryInfos.get(root.getName());
		} else {
			parentInfo = entryInfos.get(pathname.removeLastSegments(1)
					.addTrailingSeparator().toString());
		}
		if (parentInfo == null) {// was filtered out
			return;
		}
		parentInfo.children.add(entry);
		info.parent = parentInfo.zipEntry;
		entryInfos.put(entry.getName(), info);
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
		return entryInfos.get(parent.getName()).children;
	}

	/**
	 * Returns the number of entries in this ZIP structure (ignores entries
	 * filtered out by the filter on this structure if it exists).
	 * 
	 * @return the number of entries in this ZIP structure.
	 */
	public int getEntryCount() {
		return entryInfos.size();
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
		ZipEntryInfo info = entryInfos.get(entry.getName());
		if (info != null) {
			return info.parent;
		}
		return null;

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
		entryInfos.put(root.getName(), new ZipEntryInfo(root));
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			IPath path = new Path(entry.getName()).addTrailingSeparator();
			System.out.println("caching entry " + entry.getName());
			System.out.println("caching path  " + entry.getName());

			if (entry.isDirectory()) {
				cacheDir(path);
			} else {
				// Initialize the cache for all levels above this until we reach
				// a directory that has already been added.
				int pathSegmentCount = path.segmentCount();
				if (pathSegmentCount > 1) {
					ZipEntryInfo parentInfo = cacheDir(path
							.uptoSegment(pathSegmentCount - 1));
					if (parentInfo != null) {
						cacheFile(entry);
					}
				} else {
					cacheFile(entry);
				}
			}
		}
	}
}
