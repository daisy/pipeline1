package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.eclipse.core.commands.operations.IUndoableOperation;

public class MoveUpAction extends MoveAction {

    public MoveUpAction(JobsView view) {
        super(view, "Move Up", PipelineGuiPlugin.getIcon(IIconsKeys.GO_UP));
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
        return new MoveOperation(index, index - 1, selection);
    }

}
