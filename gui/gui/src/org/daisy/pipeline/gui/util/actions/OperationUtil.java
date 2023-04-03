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
package org.daisy.pipeline.gui.util.actions;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.util.Messages;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * A set of utility methods to use the RCP undo/redo framework. Contains method
 * to execute undoable operations with default context and undo history.
 * 
 * @author Romain Deltour
 * 
 */
public final class OperationUtil {

	/**
	 * Executes the given undoable operation in the contxt of the shell of the
	 * active workbench window and within the default operation history and with
	 * no progress
	 * 
	 * @param operation
	 *            the undoable operation to execute
	 * @see OperationUtil#execute(IUndoableOperation, Shell)
	 * @see IOperationHistory#execute(IUndoableOperation, IProgressMonitor,
	 *      IAdaptable)
	 */
	public static void execute(IUndoableOperation operation) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell();
		execute(operation, shell);

	}

	/**
	 * Executes the given undoable operation in the context of the given shell
	 * and within the default operation history and with no progress
	 * 
	 * @param operation
	 *            the undoable operation to execute
	 * @param shell
	 *            the UI context for the operation
	 * @see IOperationHistory#execute(IUndoableOperation, IProgressMonitor,
	 *      IAdaptable)
	 */
	public static void execute(IUndoableOperation operation, Shell shell) {
		execute(operation, shell, null);
	}

	/**
	 * Executes the given undoable operation in the context of the given shell
	 * and within the default operation history and tracked by the given
	 * progress monitor
	 * 
	 * @param operation
	 *            the undoable operation to execute
	 * @param shell
	 *            the UI context for the operation
	 * @param monitor
	 *            the progress monitor
	 * @see IOperationHistory#execute(IUndoableOperation, IProgressMonitor,
	 *      IAdaptable)
	 */
	public static void execute(IUndoableOperation operation, final Shell shell,
			IProgressMonitor monitor) {

		// Get undo/redo history and context
		IOperationHistory history = OperationHistoryFactory
				.getOperationHistory();
		IUndoContext context = PlatformUI.getWorkbench().getOperationSupport()
				.getUndoContext();
		operation.addContext(context);

		// Create an adapter for providing UI context to the operation.
		IAdaptable info = new IAdaptable() {
			@SuppressWarnings("unchecked") //$NON-NLS-1$
			public Object getAdapter(Class adapter) {
				if (Shell.class.equals(adapter)) {
					return shell;
				}
				return null;
			}
		};

		try {
			history.execute(operation, null, info);
		} catch (ExecutionException e) {
			// TODO implement better exception dialog
			MessageDialog.openError(shell, NLS.bind(
					Messages.dialog_operationError, operation.getLabel()), e
					.getMessage());
			GuiPlugin.get().error(e.getMessage(), e);
		}
	}

	// Not instantiable static utility
	private OperationUtil() {
		super();
	}
}
