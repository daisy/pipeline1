package org.daisy.pipeline.gui;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
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
    private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;
    private IWorkbenchAction showViewAction;
    private IWorkbenchAction undoAction;
    private IWorkbenchAction redoAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(final IWorkbenchWindow window) {
        // Creates the actions and registers them.
        // Registering is needed to ensure that key bindings work.
        // The corresponding commands keybindings are defined in the plugin.xml
        // file.
        // Registering also provides automatic disposal of the actions when
        // the window is closed.

        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        showViewAction = ActionFactory.SHOW_VIEW_MENU.create(window);
        register(showViewAction);
        undoAction = ActionFactory.UNDO.create(window);
        undoAction.setImageDescriptor(ApplicationIcons.getImageDescriptor(ApplicationIcons.UNDO));
        register(undoAction);
        redoAction = ActionFactory.REDO.create(window);
        redoAction.setImageDescriptor(ApplicationIcons.getImageDescriptor(ApplicationIcons.REDO));
        register(redoAction);
    }

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
        fileMenu.add(exitAction);

        // Edit menu
        editMenu.add(undoAction);
        editMenu.add(redoAction);

        // Window menu
        windowMenu.add(showViewAction);

        // Help menu
        helpMenu.add(aboutAction);
    }

}
