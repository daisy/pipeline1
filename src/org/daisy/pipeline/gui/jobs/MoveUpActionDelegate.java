package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.jobs.model.Job;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

public class MoveUpActionDelegate implements IViewActionDelegate, IWorkbenchWindowActionDelegate {

    private IStructuredSelection selection;

    public void init(IViewPart view) {
        // Nothing
    }

    public void init(IWorkbenchWindow window) {
        // Nothing
    }

    public void run(IAction action) {
        IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
        IUndoContext undoContext = PlatformUI.getWorkbench().getOperationSupport().getUndoContext();
        IUndoableOperation operation = new MoveUpOperation("Move Up");
        operation.addContext(undoContext);
        try {
            operationHistory.execute(operation, null, null);
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void selectionChanged(IAction action, ISelection incoming) {
        // Selection containing elements
        if (incoming instanceof IStructuredSelection) {
            selection = (IStructuredSelection) incoming;
            action.setEnabled(selection.size() == 1 && (selection.getFirstElement()) instanceof Job);
        } else {
            // Other selections, for example containing text or of other kinds.
            action.setEnabled(false);
        }

    }

    public void dispose() {
        // Nothing
    }
}
