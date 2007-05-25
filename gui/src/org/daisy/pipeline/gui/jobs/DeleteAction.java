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

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.JobManager;
import org.daisy.pipeline.gui.util.viewers.DefaultSelectionEnabler;
import org.daisy.pipeline.gui.util.viewers.ISelectionEnabler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.PlatformUI;

public class DeleteAction extends Action implements ISelectionChangedListener {
    private final JobsView view;
    private final JobManager jobManager;
    private final ISelectionEnabler enabler;
    private IStructuredSelection selection;

    public DeleteAction(JobsView view) {
        super(Messages.action_delete);
        this.view = view;
        this.jobManager = JobManager.getDefault();
        this.enabler = new DefaultSelectionEnabler(
                ISelectionEnabler.Mode.ONE_OR_MORE,
                new Class[] { JobInfo.class });
        setEnabled(false);
        this.view.getViewer().addSelectionChangedListener(this);
    }

    @Override
    public void run() {
        IOperationHistory operationHistory = OperationHistoryFactory
                .getOperationHistory();
        IUndoContext undoContext = PlatformUI.getWorkbench()
                .getOperationSupport().getUndoContext();
        IUndoableOperation operation = new DeleteOperation(selection);
        operation.addContext(undoContext);
        try {
            // No need to provide monitor or GUI context
            operationHistory.execute(operation, null, null);
        } catch (ExecutionException e) {
            // TODO implement better exception dialog
            MessageDialog.openError(view.getSite().getShell(), Messages.error_delete_title,
                    Messages.error_delete_message + e.getMessage());
        }
    }

    public void selectionChanged(SelectionChangedEvent event) {
        ISelection incoming = event.getSelection();
        setEnabled(enabler.isEnabledFor(incoming));
        if (isEnabled()) {
            selection = (IStructuredSelection) incoming;
        }

    }

    protected class DeleteOperation extends AbstractOperation {

        private IStructuredSelection sel;
        private SortedMap<Integer, JobInfo> map;

        public DeleteOperation(IStructuredSelection selection) {
            super(getText());
            sel = selection;
            map = new TreeMap<Integer, JobInfo>();
            for (Iterator iter = selection.iterator(); iter.hasNext();) {
                JobInfo job = (JobInfo) iter.next();
                map.put(jobManager.indexOf(job), job);
            }
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException {
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException {
            jobManager.removeAll(map.values());
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException {
            for (Integer index : map.keySet()) {
                jobManager.add(index, map.get(index));
            }
            view.getViewer().setSelection(sel);
            return Status.OK_STATUS;
        }

    }
}
