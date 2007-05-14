package org.daisy.pipeline.gui.util.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

public class ExpandAllAction extends Action {

    private TreeViewer viewer;

    public ExpandAllAction(TreeViewer viewer) {
        super("Expand All");
        this.viewer = viewer;
    }

    @Override
    public void run() {
        viewer.expandAll();
    }
}