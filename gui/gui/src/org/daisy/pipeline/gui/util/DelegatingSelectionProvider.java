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
package org.daisy.pipeline.gui.util;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * <p>
 * IPostSelectionProvider implementation that delegates to another
 * ISelectionProvider or IPostSelectionProvider. The selection provider used for
 * delegation can be exchanged dynamically. Registered listeners are adjusted
 * accordingly. This utility class may be used in workbench parts with multiple
 * viewers.
 * </p>
 * 
 * <p>
 * This class comes from the <a
 * href="http://www.eclipse.org/articles/Article-WorkbenchSelections/DelegatingSelectionProvider.java">
 * implementation </a> found in the article <a
 * href="http://www.eclipse.org/articles/Article-WorkbenchSelections/article.html">
 * Eclipse Workbench: Using the Selection Service </a>
 * </p>
 * 
 * @author Marc R. Hoffmann
 */
public class DelegatingSelectionProvider implements IPostSelectionProvider {

    private final ListenerList selectionListeners = new ListenerList();

    private final ListenerList postSelectionListeners = new ListenerList();

    private ISelectionProvider delegate;

    private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
        public void selectionChanged(SelectionChangedEvent event) {
            if (event.getSelectionProvider() == delegate) {
                fireSelectionChanged(event.getSelection());
            }
        }
    };

    private ISelectionChangedListener postSelectionListener = new ISelectionChangedListener() {
        public void selectionChanged(SelectionChangedEvent event) {
            if (event.getSelectionProvider() == delegate) {
                firePostSelectionChanged(event.getSelection());
            }
        }
    };

    /**
     * Sets a new selection provider to delegate to. Selection listeners
     * registered with the previous delegate are removed before.
     * 
     * @param newDelegate new selection provider
     */
    public void setSelectionProviderDelegate(ISelectionProvider newDelegate) {
        if (delegate != null) {
            delegate.removeSelectionChangedListener(selectionListener);
            if (delegate instanceof IPostSelectionProvider) {
                ((IPostSelectionProvider) delegate)
                        .removePostSelectionChangedListener(postSelectionListener);
            }
        }
        delegate = newDelegate;
        if (newDelegate != null) {
            newDelegate.addSelectionChangedListener(selectionListener);
            if (newDelegate instanceof IPostSelectionProvider) {
                ((IPostSelectionProvider) newDelegate)
                        .addPostSelectionChangedListener(postSelectionListener);
            }
            fireSelectionChanged(newDelegate.getSelection());
        }
    }

    protected void fireSelectionChanged(ISelection selection) {
        fireSelectionChanged(selectionListeners, selection);
    }

    protected void firePostSelectionChanged(ISelection selection) {
        fireSelectionChanged(postSelectionListeners, selection);
    }

    private void fireSelectionChanged(ListenerList list, ISelection selection) {
        SelectionChangedEvent event = new SelectionChangedEvent(delegate,
                selection);
        Object[] listeners = list.getListeners();
        for (int i = 0; i < listeners.length; i++) {
            ISelectionChangedListener listener = (ISelectionChangedListener) listeners[i];
            listener.selectionChanged(event);
        }
    }

    // IPostSelectionProvider Implementation

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        selectionListeners.add(listener);
    }

    public void removeSelectionChangedListener(
            ISelectionChangedListener listener) {
        selectionListeners.remove(listener);
    }

    public void addPostSelectionChangedListener(
            ISelectionChangedListener listener) {
        postSelectionListeners.add(listener);
    }

    public void removePostSelectionChangedListener(
            ISelectionChangedListener listener) {
        postSelectionListeners.remove(listener);
    }

    public ISelection getSelection() {
        return delegate == null ? null : delegate.getSelection();
    }

    public void setSelection(ISelection selection) {
        if (delegate != null) {
            delegate.setSelection(selection);
        }
    }

}
