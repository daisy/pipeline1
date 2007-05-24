package org.daisy.pipeline.gui.util.actions;

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
 * @author Romain Deltour
 * 
 */
public final class OperationUtil {

    private OperationUtil() {
        super();
    }

    public static void execute(IUndoableOperation operation) {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getShell();
        execute(operation, shell);

    }

    public static void execute(IUndoableOperation operation, Shell shell) {
        execute(operation, shell, null);
    }

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
            public Object getAdapter(Class adapter) {
                if (Shell.class.equals(adapter))
                    return shell;
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
        }
    }
}
