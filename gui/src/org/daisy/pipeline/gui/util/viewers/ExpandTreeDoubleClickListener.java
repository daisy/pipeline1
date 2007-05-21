package org.daisy.pipeline.gui.util.viewers;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Romain Deltour
 * 
 */
public class ExpandTreeDoubleClickListener implements IDoubleClickListener {

    public void doubleClick(DoubleClickEvent event) {
        ISelection sel = event.getSelection();
        Viewer viewer = event.getViewer();
        if (sel instanceof IStructuredSelection && viewer instanceof TreeViewer) {
            ((TreeViewer) viewer).expandToLevel(((IStructuredSelection) sel)
                    .getFirstElement(), 1);

        }
    }
}