package org.daisy.pipeline.gui.jobs;

import org.daisy.dmfc.qmanager.Job;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

public class MoveUpAction extends Action implements ISelectionListener, ActionFactory.IWorkbenchAction {
    private final IWorkbenchWindow window;
    private IStructuredSelection selection;

    public MoveUpAction(final IWorkbenchWindow window) {
        super();
        this.window = window;
        window.getSelectionService().addSelectionListener(this);
    }

    @Override
    public void run() {
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

    public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
        // Selection containing elements
        if (incoming instanceof IStructuredSelection) {
            selection = (IStructuredSelection) incoming;
            setEnabled(selection.size() == 1 && (selection.getFirstElement()) instanceof Job);
        } else {
            // Other selections, for example containing text or of other kinds.
            setEnabled(false);
        }
    }

    public void dispose() {
        window.getSelectionService().removeSelectionListener(this);
    }
}
