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
package org.daisy.pipeline.gui.messages;

import javax.xml.stream.Location;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.model.IMessageManagerListener;
import org.daisy.pipeline.gui.model.MessageManager;
import org.daisy.pipeline.gui.util.Category;
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

    private MessagesView view;
    private TreeViewer viewer;
    private MessageManager manager;

    public MessagesContentProvider(MessagesView view) {
        super();
        this.view = view;
    }

    @Override
    public void dispose() {
        if (manager != null) {
            manager.removeMessageManagerListener(this);
        }
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof MessageEvent) {
            Location loc = ((MessageEvent) parentElement).getLocation();
            if (loc != null) {
                return new Location[] { loc };
            }
        }
        return super.getChildren(parentElement);
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof Location) {
            // TODO return parent
        }
        return super.getParent(element);
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof MessageEvent) {
            return ((MessageEvent) element).getLocation() != null;
        }
        return super.hasChildren(element);
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
        // TODO set ui job family
        Job uiJob = new WorkbenchJob(Messages.uiJob_addMessage_name) {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                if (isCategorized()) {
                    Category category = findCategory(message);
                    if (!category.isVisible()) {
                        viewer.add(viewer.getInput(), category);
                    }
                    viewer.add(category, message);
                } else {
                    viewer.add(viewer.getInput(), message);
                }
                if (!view.isLocked()) {
                    viewer.reveal(message);
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
