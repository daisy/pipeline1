package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.JobManager;
import org.daisy.pipeline.gui.util.actions.OperationUtil;
import org.daisy.pipeline.gui.util.viewers.DefaultSelectionEnabler;
import org.daisy.pipeline.gui.util.viewers.ISelectionEnabler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IPropertyListener;

public abstract class MoveAction extends Action implements
        ISelectionChangedListener, IPropertyListener {
    protected final JobsView view;
    protected Object selectedElem;
    protected ISelection selection;
    protected JobManager jobManager;
    private ISelectionEnabler enabler;

    public MoveAction(JobsView view, String text, ImageDescriptor icon) {
        super(text, icon);
        this.view = view;
        this.jobManager = JobManager.getDefault();
        this.enabler = new DefaultSelectionEnabler(
                ISelectionEnabler.Mode.SINGLE, new Class[] { JobInfo.class });
        setEnabled(false);
        this.view.getViewer().addSelectionChangedListener(this);
        this.view.addPropertyListener(this);
    }

    @Override
    public void run() {
        OperationUtil.execute(getOperation(), view.getSite().getShell());
    }

    public void selectionChanged(SelectionChangedEvent event) {
        ISelection incoming = event.getSelection();
        setEnabled(enabler.isEnabledFor(incoming));
        if (isEnabled()) {
            selection = incoming;
            selectedElem = ((IStructuredSelection) incoming)
                    .getFirstElement();
        }

    }

    protected abstract IUndoableOperation getOperation();

    public abstract void propertyChanged(Object source, int propId);

    protected class MoveOperation extends AbstractOperation {

        private final int oldIndex;
        private final int newIndex;
        private final ISelection sel;

        public MoveOperation(int oldIndex, int newIndex, ISelection sel) {
            super(getText());
            this.oldIndex = oldIndex;
            this.newIndex = newIndex;
            this.sel = sel;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException {
            redo(monitor, info);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException {
            jobManager.move(oldIndex, newIndex);
            view.firePropertyChange(JobsView.PROP_SEL_JOB_INDEX);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info)
                throws ExecutionException {
            jobManager.move(newIndex, oldIndex);
            view.firePropertyChange(JobsView.PROP_SEL_JOB_INDEX);
            view.getViewer().setSelection(sel);
            return Status.OK_STATUS;
        }

    }
}
