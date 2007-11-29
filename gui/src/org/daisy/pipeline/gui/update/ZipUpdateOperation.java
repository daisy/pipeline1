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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.util.ZipStructure;
import org.daisy.util.file.EFolder;
import org.daisy.util.file.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

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
	/** A map for retrieving backups of overwritten files */
	private Map<File, File> backupFiles;
	/** The set of top-level directories that have been created by the operation */
	private Set<File> newDirs;
	/** Keeps track of errors that occurred during the operation (can be null) */
	private MultiStatus errors;

	/**
	 * Creates a new update operation for the given ZIP structure.
	 * 
	 * @param zipStruct
	 *            the ZIP structure used for the new update operation
	 * @param shell
	 *            TODO
	 */
	public ZipUpdateOperation(ZipStructure zipStruct, Shell shell) {
		this.zipStruct = zipStruct;
		try {
			this.installDir = new File(Platform.getInstallLocation().getURL()
					.toURI());
		} catch (URISyntaxException e) {
			GuiPlugin.get().error("Unable to get a File from install location", //$NON-NLS-1$
					e);
		}
		this.backupFiles = new HashMap<File, File>();
		this.newDirs = new HashSet<File>();
	}

	private void addError(File file, Exception e) {
		GuiPlugin.get().error("An error occured while updating " + file, e); //$NON-NLS-1$
		if (errors == null) {
			errors = new MultiStatus(GuiPlugin.ID, 0,
					Messages.zipOperation_error_globalMessage, null);
		}
		errors.add(new Status(IStatus.ERROR, GuiPlugin.ID,
				((file.isDirectory()) ? Messages.zipOperation_error_directory
						: Messages.zipOperation_error_file), e));
	}

	/**
	 * Returns the status of the update operation. Any error that occurred
	 * during the execution of this operation is added as a {@link IStatus} to
	 * the returned {@link MultiStatus}.
	 * 
	 * @return the status of the update operation.
	 */
	public IStatus getStatus() {
		return (errors == null) ? Status.OK_STATUS : errors;
	}

	/**
	 * Reverts this update operation.
	 * <ul>
	 * <li>Delete any directory created by the operation.</li>
	 * <li>Revert any changed file to its backup copy.</li>
	 * </ul>
	 */
	public void revert() {
		for (File dir : newDirs) {
			try {
				EFolder folder = new EFolder(dir);
				if (!(folder.deleteContents() && folder.delete())) {
					throw new IllegalStateException(
							"Could not delete directory " + dir); //$NON-NLS-1$
				}
			} catch (Exception e) {
				GuiPlugin.get().error("Could not revert directory " + dir, e); //$NON-NLS-1$
			}
		}
		for (File file : backupFiles.keySet()) {
			try {
				FileUtils.copyFile(backupFiles.get(file), file);
			} catch (IOException e) {
				GuiPlugin.get().error("Could not revert file " + file, e); //$NON-NLS-1$
			}
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
			monitor.beginTask(Messages.zipOperation_monitor_mainTask, zipStruct
					.getEntryCount() * 10);
			updateRec(zipStruct.getRoot(), false);
		} finally {
			monitor.done();
		}

	}

	private boolean updateDir(ZipEntry entry, boolean isNewBranch) {
		monitor.subTask(Messages.zipOperation_monitor_updatingDir
				+ entry.getName());
		File dir = new File(installDir, entry.getName());
		try {
			if (!dir.exists()) {
				if (!dir.mkdir()) {
					throw new IOException(dir + " (Permission denied)"); //$NON-NLS-1$
				}
				if (!isNewBranch) {
					newDirs.add(dir);
				}
				isNewBranch = true;
			}
		} catch (Exception e) {
			addError(dir, e);
		} finally {
			monitor.worked(10);
		}
		return isNewBranch;
	}

	private void updateFile(ZipEntry entry) {
		monitor.subTask(Messages.zipOperation_monitor_updatingFile
				+ entry.getName());
		File file = new File(installDir, entry.getName());
		try {
			if (file.exists()) {
				File backup = File.createTempFile("pipeline.update-" //$NON-NLS-1$
						+ file.getName() + "-", ".bak"); //$NON-NLS-1$ //$NON-NLS-2$
				backup.deleteOnExit();
				FileUtils.copyFile(file, backup);
				backupFiles.put(file, backup);
				file.delete();
			}
			FileUtils.writeInputStreamToFile(zipStruct.getZipFile()
					.getInputStream(entry), file);
		} catch (Exception e) {
			addError(file, e);
		} finally {
			monitor.worked(10);
		}
	}

	private void updateRec(ZipEntry entry, boolean isNewBranch) {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		if (!entry.isDirectory()) {
			updateFile(entry);
		} else {
			isNewBranch |= updateDir(entry, isNewBranch);
			for (ZipEntry childEntry : zipStruct.getChildren(entry)) {
				updateRec(childEntry, isNewBranch);
			}
		}
	}
}
