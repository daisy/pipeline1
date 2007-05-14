package org.daisy.pipeline.gui.util.actions;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

public class ExpandAllAction extends Action {

    private TreeViewer viewer;

    public ExpandAllAction(TreeViewer viewer) {
        super("Expand All", GuiPlugin.createDescriptor(IIconsKeys.TREE_EXPAND_ALL));
        this.viewer = viewer;
    }

    @Override
    public void run() {
        viewer.expandAll();
    }
}