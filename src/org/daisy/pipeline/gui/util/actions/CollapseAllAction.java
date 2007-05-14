package org.daisy.pipeline.gui.util.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

public class CollapseAllAction extends Action {

    private TreeViewer viewer;

    public CollapseAllAction(TreeViewer viewer) {
        super("Collapse All");
        this.viewer = viewer;
    }

    @Override
    public void run() {
        viewer.collapseAll();
    }
}