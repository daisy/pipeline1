package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.eclipse.core.commands.operations.IUndoableOperation;

public class MoveToBottomAction extends MoveAction {

    public MoveToBottomAction(JobsView view) {
        super(view, "Move To Bottom", PipelineGuiPlugin
                .getIcon(IIconsKeys.GO_BOTTOM));
    }

    @Override
    public void propertyChanged(Object source, int propId) {
        if (propId == JobsView.PROP_SEL_JOB_INDEX) {
            setEnabled(jobManager.indexOf(selectedJob) < jobManager.size() - 1);
        }
    }

    @Override
    protected IUndoableOperation getOperation() {
        int index = jobManager.indexOf(selectedJob);
        return new MoveOperation(index, jobManager.size() - 1, selection);
    }
}
