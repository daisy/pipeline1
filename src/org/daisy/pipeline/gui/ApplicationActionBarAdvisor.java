package org.daisy.pipeline.gui;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    // Actions - important to allocate these only in makeActions, and then use
    // them
    // in the fill methods. This ensures that the actions aren't recreated
    // when fillActionBars is called with FILL_PROXY.
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction deleteAction;
    private IWorkbenchAction exitAction;
    private IWorkbenchAction newAction;
    private IWorkbenchAction redoAction;
    private IWorkbenchAction showViewAction;
    private IWorkbenchAction undoAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    @Override
    protected void makeActions(final IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml
        // file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        deleteAction = ActionFactory.DELETE.create(window);
        register(deleteAction);
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
        newAction = ActionFactory.NEW.create(window);
        register(newAction);
        redoAction = ActionFactory.REDO.create(window);
        register(redoAction);
        showViewAction = ActionFactory.SHOW_VIEW_MENU.create(window);
        register(showViewAction);
        undoAction = ActionFactory.UNDO.create(window);
        register(undoAction);
    }

    @Override
    protected void fillMenuBar(IMenuManager menuBar) {
        // Menu Managers
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
        MenuManager editMenu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
        MenuManager windowMenu = new MenuManager("&Window", IWorkbenchActionConstants.M_WINDOW);
        MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);

        // Menu Bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));// Placeholder
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);

        // File menu
        MenuManager newSubMenu = new MenuManager("&New", ActionFactory.NEW.getId());
        newSubMenu.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
        newSubMenu.add(new Separator());
        newSubMenu.add(newAction);
        fileMenu.add(newSubMenu);
        fileMenu.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);

        // Edit menu
        editMenu.add(undoAction);
        editMenu.add(redoAction);
        //TODO add cut/copy/paste actions
        editMenu.add(new Separator());
        editMenu.add(deleteAction);
        //TODO add selectAll action
        editMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        editMenu.add(new Separator());
        //TODO add find/replace action
        
        // Window menu
        windowMenu.add(showViewAction);

        // Help menu
        helpMenu.add(aboutAction);
    }

}
