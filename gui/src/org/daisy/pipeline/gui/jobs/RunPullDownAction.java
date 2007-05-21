package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.IActionConstants;
import org.daisy.pipeline.gui.util.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;

/**
 * @author Romain Deltour
 * 
 */
public class RunPullDownAction implements IWorkbenchWindowPulldownDelegate {

    IAction runAllAction;
    IAction runAction;
    MenuManager menu;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
     */
    public Menu getMenu(Control parent) {
        if (menu == null) {
            menu = new MenuManager();
            menu.add(runAction);
            menu.add(runAllAction);
        }
        return menu.createContextMenu(parent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
        if (menu != null) {
            menu.dispose();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
        // [Hack] declarative actions are retrieved from the action registry.
        // The registry must have been initialized at that point.
        runAction = ActionRegistry.getDefault().getAction(
                IActionConstants.RUN);
        runAllAction = ActionRegistry.getDefault().getAction(
                IActionConstants.RUN_ALL);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        runAction.run();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

}
