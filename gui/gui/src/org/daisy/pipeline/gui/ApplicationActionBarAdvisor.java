/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.gui;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 * <p>
 * The following advisor methods are called at strategic points in the
 * workbench's life cycle (all occur within the dynamic scope of the call to
 * {@link PlatformUI#createAndRunWorkbench PlatformUI.createAndRunWorkbench}):
 * <ul>
 * <li><code>fillActionBars</code> - called after
 * <code>WorkbenchWindowAdvisor.preWindowOpen</code> to configure a window's
 * action bars</li>
 * </ul>
 * </p>
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction aboutAction;
	private IWorkbenchAction deleteAction;
	private IWorkbenchAction exitAction;
	private IWorkbenchAction introAction;
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
	private IWorkbenchAction resetPerspAction;
	private IWorkbenchAction redoAction;
	private IWorkbenchAction showPaneMenuAction;
	private IWorkbenchAction showViewMenuAction;
	private IWorkbenchAction undoAction;
	// Contribution Items
	private IContributionItem perspListItem;
	private IContributionItem viewListItem;
	private IContributionItem wizardListItem;

	/**
	 * Creates a new action bar advisor to configure a workbench window's action
	 * bars via the given action bar configurer.
	 * 
	 * @param configurer
	 *            the action bar configurer
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
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

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		// Menu Managers
		MenuManager fileMenu = new MenuManager(Messages.menu_file,
				IWorkbenchActionConstants.M_FILE);
		MenuManager editMenu = new MenuManager(Messages.menu_edit,
				IWorkbenchActionConstants.M_EDIT);
		MenuManager windowMenu = new MenuManager(Messages.menu_window,
				IWorkbenchActionConstants.M_WINDOW);
		MenuManager helpMenu = new MenuManager(Messages.menu_help,
				IWorkbenchActionConstants.M_HELP);

		// Menu Bar
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));// Placeholder
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);

		// File menu
		MenuManager newSubMenu = new MenuManager(Messages.menu_new,
				ActionFactory.NEW.getId());
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
		MenuManager perspSubMenu = new MenuManager(
				Messages.menu_window_openPerspective, "openPerspective"); //$NON-NLS-1$
		perspSubMenu.add(perspListItem);
		MenuManager viewSubMenu = new MenuManager(
				Messages.menu_window_showView, "showView"); //$NON-NLS-1$
		viewSubMenu.add(viewListItem);
		MenuManager navSubMenu = new MenuManager(
				Messages.menu_window_navigation, "navigation"); //$NON-NLS-1$
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
		windowMenu.add(resetPerspAction);
		windowMenu.add(new Separator());
		windowMenu.add(navSubMenu);
		windowMenu.add(new Separator());
		windowMenu.add(preferencesAction);
		//

		// Help menu
		helpMenu.add(introAction);
		helpMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		helpMenu.add(new Separator());
		helpMenu.add(aboutAction);
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
		introAction = ActionFactory.INTRO.create(window);
		register(introAction);
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
		register(prevPartAction);
		prevPerspAction = ActionFactory.PREVIOUS_PERSPECTIVE.create(window);
		register(prevPerspAction);
		resetPerspAction = ActionFactory.RESET_PERSPECTIVE.create(window);
		register(resetPerspAction);
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

}
