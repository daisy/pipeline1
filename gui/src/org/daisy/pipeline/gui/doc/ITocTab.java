package org.daisy.pipeline.gui.doc;

import java.net.URI;

import org.daisy.pipeline.gui.util.swt.ITabItemProvider;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * @author Romain Deltour
 * 
 */
public interface ITocTab extends ITabItemProvider {

    public URI getURI();

    public TreeViewer getViewer();

    public boolean select(Object element);
}
