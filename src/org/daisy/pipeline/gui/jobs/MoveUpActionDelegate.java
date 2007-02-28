package org.daisy.pipeline.gui.jobs;

import org.daisy.dmfc.qmanager.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class MoveUpActionDelegate implements IViewActionDelegate, IWorkbenchWindowActionDelegate {

    private IStructuredSelection selection;

    public void init(IViewPart view) {
        // Nothing
    }

    public void init(IWorkbenchWindow window) {
        // Nothing
    }

    public void run(IAction action) {
        System.out.println("move up");
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
