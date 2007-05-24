package org.daisy.pipeline.gui.doc;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * @author Romain Deltour
 * 
 */
public class SyncTocAction extends Action implements IAction {

    private DocView view;

    public SyncTocAction(DocView view) {
        super(Messages.action_synchronize, IAction.AS_CHECK_BOX);
        setText(Messages.action_synchronize);
        setToolTipText(Messages.action_synchronize_tooltip);
        setImageDescriptor(GuiPlugin
                .createDescriptor(IIconsKeys.HELP_SYNCHRONIZE));
        this.view = view;
        setChecked(view.shouldSyncToc());
    }

    @Override
    public void run() {
        view.setSyncToc(isChecked());
    }

}
