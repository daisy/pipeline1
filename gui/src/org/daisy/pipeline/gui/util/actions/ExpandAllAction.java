package org.daisy.pipeline.gui.util.actions;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.util.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

public class ExpandAllAction extends Action {

    private TreeViewer viewer;

    public ExpandAllAction(TreeViewer viewer) {
        super(Messages.action_expandAll, GuiPlugin.createDescriptor(IIconsKeys.TREE_EXPAND_ALL));
        this.viewer = viewer;
    }

    @Override
    public void run() {
        viewer.expandAll();
    }
}