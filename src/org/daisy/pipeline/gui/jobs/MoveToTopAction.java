package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.GuiPlugin;
import org.eclipse.core.commands.operations.IUndoableOperation;

public class MoveToTopAction extends MoveAction {

    public MoveToTopAction(JobsView view) {
        super(view, "Move To Top", GuiPlugin.getIcon(IIconsKeys.GO_TOP));
    }

    @Override
    public void propertyChanged(Object source, int propId) {
        if (propId == JobsView.PROP_SEL_JOB_INDEX) {
            setEnabled(jobManager.indexOf(selectedElem) > 0);
        }
    }

    @Override
    protected IUndoableOperation getOperation() {
        int index = jobManager.indexOf(selectedElem);
        return new MoveOperation(index, 0, selection);
    }
}
