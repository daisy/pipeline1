package org.daisy.pipeline.gui;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
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
    private IWorkbenchAction maximizeAction;
    private IWorkbenchAction minimizeAction;
    private IWorkbenchAction navBackAction;
    private IWorkbenchAction navForwardAction;
    private IWorkbenchAction newAction;
    private IWorkbenchAction nextPartAction;
    private IWorkbenchAction nextPerspAction;
    private IWorkbenchAction preferencesAction;
    private IWorkbenchAction prevPartAction;
    private IWorkbenchAction prevPerspAction;
    private IWorkbenchAction redoAction;
    private IWorkbenchAction showPaneMenuAction;
    private IWorkbenchAction showViewMenuAction;
    private IWorkbenchAction undoAction;
    // Contribution Items
    private IContributionItem perspListItem;
    private IContributionItem viewListItem;
    private IContributionItem wizardListItem;

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

        // Create Actions
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);
        deleteAction = ActionFactory.DELETE.create(window);
        register(deleteAction);
        exitAction = ActionFactory.QUIT.create(window);
        register(exitAction);
        maximizeAction = ActionFactory.MAXIMIZE.create(window);
        register(maximizeAction);
        minimizeAction = ActionFactory.MINIMIZE.create(window);
        register(minimizeAction);
        navBackAction = ActionFactory.BACK.create(window);
        register(navBackAction);
        navForwardAction = ActionFactory.FORWARD.create(window);
        register(navForwardAction);
        newAction = ActionFactory.NEW_WIZARD_DROP_DOWN.create(window);
        register(newAction);
        nextPartAction = ActionFactory.NEXT_PART.create(window);
        register(nextPartAction);
        nextPerspAction = ActionFactory.NEXT_PERSPECTIVE.create(window);
        register(nextPerspAction);
        preferencesAction = ActionFactory.PREFERENCES.create(window);
        register(preferencesAction);
        prevPartAction = ActionFactory.PREVIOUS_PART.create(window);
        register(nextPartAction);
        prevPerspAction = ActionFactory.PREVIOUS_PERSPECTIVE.create(window);
        register(nextPartAction);
        redoAction = ActionFactory.REDO.create(window);
        register(redoAction);
        showPaneMenuAction = ActionFactory.SHOW_PART_PANE_MENU.create(window);
        register(showPaneMenuAction);
        showViewMenuAction = ActionFactory.SHOW_VIEW_MENU.create(window);
        register(showViewMenuAction);
        undoAction = ActionFactory.UNDO.create(window);
        register(undoAction);
        undoAction = ActionFactory.UNDO.create(window);
        register(undoAction);

        // Create Contribution Items
        perspListItem = ContributionItemFactory.PERSPECTIVES_SHORTLIST
                .create(window);
        viewListItem = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
        wizardListItem = ContributionItemFactory.NEW_WIZARD_SHORTLIST
                .create(window);
    }

    @Override
    protected void fillMenuBar(IMenuManager menuBar) {
        // Menu Managers
        MenuManager fileMenu = new MenuManager("&File",
                IWorkbenchActionConstants.M_FILE);
        MenuManager editMenu = new MenuManager("&Edit",
                IWorkbenchActionConstants.M_EDIT);
        MenuManager windowMenu = new MenuManager("&Window",
                IWorkbenchActionConstants.M_WINDOW);
        MenuManager helpMenu = new MenuManager("&Help",
                IWorkbenchActionConstants.M_HELP);

        // Menu Bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));// Placeholder
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);

        // File menu
        MenuManager newSubMenu = new MenuManager("&New", ActionFactory.NEW
                .getId());
        newSubMenu.add(wizardListItem);
        fileMenu.add(newSubMenu);
        fileMenu.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
        fileMenu.add(new Separator());
        fileMenu.add(exitAction);

        // Edit menu
        editMenu.add(undoAction);
        editMenu.add(redoAction);
        // TODO add cut/copy/paste actions
        editMenu.add(new Separator());
        editMenu.add(deleteAction);
        // TODO add selectAll action
        editMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        editMenu.add(new Separator());
        // TODO add find/replace action

        // Window menu
        MenuManager perspSubMenu = new MenuManager("Open &Perspective",
                "openPerspective");
        perspSubMenu.add(perspListItem);
        MenuManager viewSubMenu = new MenuManager("Show &View", "showView");
        viewSubMenu.add(viewListItem);
        MenuManager navSubMenu = new MenuManager("&Navigation", "navigation");
        navSubMenu.add(showPaneMenuAction);
        navSubMenu.add(showViewMenuAction);
        navSubMenu.add(new Separator());
        navSubMenu.add(maximizeAction);
        navSubMenu.add(minimizeAction);
        navSubMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        navSubMenu.add(new Separator());
        navSubMenu.add(nextPartAction);
        navSubMenu.add(prevPartAction);
        navSubMenu.add(new Separator());
        navSubMenu.add(nextPerspAction);
        navSubMenu.add(prevPerspAction);
        windowMenu.add(perspSubMenu);
        windowMenu.add(viewSubMenu);
        windowMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        windowMenu.add(new Separator());
        windowMenu.add(navSubMenu);
        windowMenu.add(new Separator());
        windowMenu.add(preferencesAction);
        //

        // Help menu
        helpMenu.add(aboutAction);
    }

    @Override
    protected void fillCoolBar(ICoolBarManager coolBar) {
        coolBar.add(new GroupMarker(IWorkbenchActionConstants.TOOLBAR_FILE));
        coolBar.appendToGroup(IWorkbenchActionConstants.TOOLBAR_FILE,
                deleteAction);
        // ToolBarManager fileBar = new ToolBarManager();
        // fileBar.
        // fileBar.add(new Separator("file"));
        // coolBar.add(fileBar);
        // Edit tool bar
        // ToolBarManager editBar = new ToolBarManager();
        // editBar.add(deleteAction);
        // coolBar.add(editBar);
    }

}
