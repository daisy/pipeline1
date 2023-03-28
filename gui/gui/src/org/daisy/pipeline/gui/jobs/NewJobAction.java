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
