package org.daisy.pipeline.gui.util.viewers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

public class CompositeLabelProvider extends EventManager implements
        ICompositeLabelProvider {
    /**
     * Creates a new label provider.
     */
    public CompositeLabelProvider() {
    }

    /**
     * The <code>CompositeLabelProvider</code> implementation of this
     * <code>ICompositeLabelProvider</code> method returns <code>null</code>.
     * Subclasses may override.
     */
    public Image getImage(String key, Object element) {
        return null;
    }

    /**
     * The <code>CompositeLabelProvider</code> implementation of this
     * <code>ICompositeLabelProvider</code> method returns an empty set.
     * Subclasses may override.
     */
    public Set<String> getImageKeys() {
        return new HashSet<String>(0);
    }

    /**
     * The <code>CompositeLabelProvider</code> implementation of this
     * <code>ICompositeLabelProvider</code> method returns 0. Subclasses may
     * override.
     */
    public int getInt(String key, Object element) {
        return 0;
    }

    /**
     * The <code>CompositeLabelProvider</code> implementation of this
     * <code>ICompositeLabelProvider</code> method returns an empty set.
     * Subclasses may override.
     */
    public Set<String> getIntKeys() {
        return new HashSet<String>(0);
    }

    /**
     * The <code>CompositeLabelProvider</code> implementation of this
     * <code>ICompositeLabelProvider</code> method returns an empty string.
     * Subclasses may override.
     */
    public String getText(String key, Object element) {
        return ""; //$NON-NLS-1$
    }

    /**
     * The <code>CompositeLabelProvider</code> implementation of this
     * <code>ICompositeLabelProvider</code> method returns an empty set.
     * Subclasses may override.
     */
    public Set<String> getTextKeys() {
        return new HashSet<String>(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void addListener(ILabelProviderListener listener) {
        addListenerObject(listener);
    }

    /**
     * The <code>CompositeLabelProvider</code> implementation of this
     * <code>IBaseLabelProvider</code> method does nothing. Subclasses may
     * extend.
     */
    public void dispose() {
    }

    /**
     * The <code>CompositeLabelProvider</code> implementation of this
     * <code>IBaseLabelProvider</code> method returns <code>true</code>.
     * Subclasses may override.
     */
    public boolean isLabelProperty(Object element, String property) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
     */
    public void removeListener(ILabelProviderListener listener) {
        removeListenerObject(listener);

    }

    /**
     * Fires a label provider changed event to all registered listeners Only
     * listeners registered at the time this method is called are notified.
     * 
     * @param event a label provider changed event
     * 
     * @see ILabelProviderListener#labelProviderChanged
     */
    protected void fireLabelProviderChanged(
            final LabelProviderChangedEvent event) {
        Object[] listeners = getListeners();
        for (int i = 0; i < listeners.length; ++i) {
            final ILabelProviderListener l = (ILabelProviderListener) listeners[i];
            SafeRunnable.run(new SafeRunnable() {
                public void run() {
                    l.labelProviderChanged(event);
                }
            });

        }
    }
}
