package org.daisy.pipeline.gui.util.swt;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;

/**
 * @author Romain Deltour
 * 
 */
public abstract class TreeTabItemProvider extends DefaultTabItemProvider {

    private TreeViewer treeViewer;

    @Override
    protected Control createControl(TabFolder parent) {
        treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.BORDER);
        treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        treeViewer.setContentProvider(createContentProvider());
        treeViewer.setLabelProvider(createLabelProvider());
        treeViewer.setInput(getInput());
        return treeViewer.getControl();
    }

    public TreeViewer getViewer() {
        return treeViewer;
    }

    @Override
    protected abstract String getTitle();

    protected abstract IContentProvider createContentProvider();

    protected abstract Object getInput();

    protected abstract IBaseLabelProvider createLabelProvider();

    @Override
    protected abstract String getToolTipText();

}