package org.daisy.pipeline.gui.messages;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.util.viewers.CategorizedContentProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @author Romain Deltour
 * 
 */
public class MessagesContentProvider extends CategorizedContentProvider
        implements IMessageManagerListener {

    private TreeViewer viewer;
    private MessageManager manager;

    @Override
    public void dispose() {
        if (manager != null) {
            manager.removeMessageManagerListener(this);
        }
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = (TreeViewer) viewer;
        if (manager != null) {
            manager.removeMessageManagerListener(this);
        }
        manager = (MessageManager) newInput;
        if (manager != null) {
            manager.addMessageManagerListener(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.messages.IMessageManagerListener#messageAdded(org.daisy.dmfc.core.event.MessageEvent)
     */
    public void messageAdded(final MessageEvent message) {
        //TODO set ui job family
        Job uiJob = new WorkbenchJob("Add Message") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                if (isCategorized()) {
                    viewer.add(findCategory(message), message);
                } else {
                    viewer.add(MessagesContentProvider.this.manager, message);
                }
                return Status.OK_STATUS;
            }

        };
        uiJob.setSystem(true);
        uiJob.schedule();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.util.CategorizedContentProvider#getAllElements()
     */
    @Override
    protected Object[] getAllElements() {
        if (manager != null) {
            return manager.getMessages().toArray();
        }
        return new Object[0];
    }
}
