package org.daisy.pipeline.gui.jobs;

import org.daisy.dmfc.qmanager.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
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
        System.out.println("Move Up Retargetable");
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

    public void init(IWorkbenchWindow window) {
        // TODO Auto-generated method stub

    }

    public void run(IAction action) {
        System.out.println("Move Up Delegate");

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
}
