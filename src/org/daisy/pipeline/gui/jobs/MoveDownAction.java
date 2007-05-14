package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.GuiPlugin;
import org.eclipse.core.commands.operations.IUndoableOperation;

public class MoveDownAction extends MoveAction {

    public MoveDownAction(JobsView view) {
        super(view, "Move Down", GuiPlugin.createDescriptor(IIconsKeys.MOVE_DOWN));
    }

    @Override
    public void propertyChanged(Object source, int propId) {
        if (propId == JobsView.PROP_SEL_JOB_INDEX) {
            setEnabled(jobManager.indexOf(selectedElem) < jobManager.size() - 1);
        }
    }

    @Override
    protected IUndoableOperation getOperation() {
        int index = jobManager.indexOf(selectedElem);
        return new MoveOperation(index, index + 1, selection);
    }

}
