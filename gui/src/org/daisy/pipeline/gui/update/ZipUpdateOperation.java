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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.zip.ZipEntry;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.util.ZipStructure;
import org.daisy.util.file.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * A runnable operation that perform a software update from a ZIP update patch.
 * <p>
 * This operation extracts the content of the ZIP in the root directory of the
 * installation and overwrites any already existing files.
 * </p>
 * <p>
 * If any error occurred during the execution, a list of error messages can be
 * retrieved at the end of the execution.
 * </p>
 * <p>
 * Each overwritten file is copied in the default temporary directory until the
 * JVM exits so that the operation can be reverted after its execution.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public class ZipUpdateOperation implements IRunnableWithProgress {
	/** The progress monitor used throughout this operation */
	private IProgressMonitor monitor;
	/** The ZIP structure used by this operation */
	private ZipStructure zipStruct;
	/** The root directory of the DAISY Pipeline installation */
	private File installDir;

	/**
	 * Creates a new update operation for the given ZIP structure.
	 * 
	 * @param zipStruct
	 *            the ZIP structure used for the new update operation
	 */
	public ZipUpdateOperation(ZipStructure zipStruct) {
		this.zipStruct = zipStruct;
		try {
			this.installDir = new File(Platform.getInstallLocation().getURL()
					.toURI());
		} catch (URISyntaxException e) {
			GuiPlugin.get().error("Unable to get a File from install location",
					e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(IProgressMonitor progressMonitor)
			throws InvocationTargetException, InterruptedException {
		monitor = progressMonitor;
		try {
			monitor.beginTask("Applying update patch", zipStruct
					.getEntryCount() * 10 + 10);
			for (ZipEntry entry : zipStruct.getChildren(zipStruct.getRoot())) {
				updateRec(entry);
			}
			zipStruct.getZipFile().close();
			monitor.worked(10);
		} catch (Exception e) {
			GuiPlugin.get().error(
					"Unexpected exception while applyinh an update patch", e);
		} finally {
			monitor.done();
		}

	}

	private void updateDir(ZipEntry entry) {
		monitor.subTask("Updating directory " + entry.getName());
		File dir = new File(installDir, entry.getName());
		if (!dir.exists() && !dir.mkdir()) {
			GuiPlugin.get().error("Couldn't create directory " + dir, null);
		}
		monitor.worked(10);
	}

	private void updateFile(ZipEntry entry) {
		monitor.subTask("Updating file " + entry.getName());
		File file = new File(installDir, entry.getName());
		if (file.exists() && !file.delete()) {
			GuiPlugin.get().error("Couldn't delete file " + file, null);
		}
		try {
			FileUtils.writeInputStreamToFile(zipStruct.getZipFile()
					.getInputStream(entry), file);
		} catch (IOException e) {
			GuiPlugin.get().error("Couldn't write to file " + file, e);
		} finally {
			monitor.worked(10);
		}
	}

	private void updateRec(ZipEntry entry) {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		if (!entry.isDirectory()) {
			updateFile(entry);
		} else {
			updateDir(entry);
			for (ZipEntry childEntry : zipStruct.getChildren(entry)) {
				updateRec(childEntry);
			}
		}
	}
}
