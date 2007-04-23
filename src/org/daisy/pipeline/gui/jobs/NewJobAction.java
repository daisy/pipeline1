package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.jobs.wizard.NewJobWizard;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class NewJobAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;
    private IStructuredSelection selection;

    public void dispose() {
        // Nothing
    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void run(IAction action) {
        NewJobWizard wizard = new NewJobWizard();
        wizard.init(window.getWorkbench(), selection);
        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.open();
    }

    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection instanceof IStructuredSelection ? (IStructuredSelection) selection
                : null;
    }

}
