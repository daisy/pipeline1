package org.daisy.pipeline.gui.jobs;

import java.util.ArrayList;
import java.util.List;

import org.daisy.pipeline.gui.jobs.model.Job;
import org.daisy.pipeline.gui.util.ISelectionEnabler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

public abstract class AbstractJobAction extends Action implements
        ActionFactory.IWorkbenchAction, ISelectionListener {
    protected final JobsView view;
    protected List<Job> selectedJobs;

    public AbstractJobAction(IViewPart view, String text, ImageDescriptor icon) {
        super(text, icon);
        this.view = (JobsView) view;
        this.selectedJobs = new ArrayList<Job>();
        view.getSite().getWorkbenchWindow().getSelectionService()
                .addSelectionListener(this);
    }

    protected abstract IUndoableOperation getOperation();

    protected abstract ISelectionEnabler getEnabler();

    @Override
    public void run() {
        IOperationHistory operationHistory = OperationHistoryFactory
                .getOperationHistory();
        IUndoContext undoContext = PlatformUI.getWorkbench()
                .getOperationSupport().getUndoContext();
        IUndoableOperation operation = getOperation();
        operation.addContext(undoContext);
        try {
            // No need to provide monitor or GUI context
            operationHistory.execute(operation, null, null);
        } catch (ExecutionException e) {
            // TODO implement better exception dialog
            MessageDialog.openError(view.getSite().getShell(),
                    "Move Job Error", "Exception while moving job: "
                            + e.getMessage());
        }
    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
        setEnabled(getEnabler().isEnabledFor(incoming));
        if (isEnabled()) {
            IStructuredSelection selection = (IStructuredSelection) incoming;
            selectedJobs = new ArrayList<Job>(selection.size());
            for (Object elem : ((IStructuredSelection) incoming).toArray()) {
                selectedJobs.add((Job) elem);
            }
        }
    }

    public void dispose() {
        view.getSite().getWorkbenchWindow().getSelectionService()
                .removeSelectionListener(this);
    }
}
