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
package org.daisy.pipeline.gui.jobs.wizard;

import java.util.ArrayList;
import java.util.List;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.ICommandConstants;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.util.actions.BrowserBackAction;
import org.daisy.pipeline.gui.util.actions.BrowserForwardAction;
import org.daisy.pipeline.gui.util.actions.ToggleBrowserAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ActiveShellExpression;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * A dialog tray that can display the documentation page about a Pipeline
 * script. This tray can be opened from the New Job Wizard.
 * 
 * @author Romain Deltour
 * 
 */
public class HelpDialogTray extends DialogTray implements IPageChangedListener {

	/**
	 * The action used to hide the <code>HelpDialogTray</code>.
	 */
	private class CloseAction extends Action {

		/**
		 * Creates the action.
		 */
		public CloseAction() {
			super();
			setText(Messages.helpTray_close);
			setToolTipText(Messages.helpTray_close_tooltip);
			setImageDescriptor(GuiPlugin
					.createDescriptor(IIconsKeys.ACTION_CLOSE));
			setHoverImageDescriptor(GuiPlugin
					.getDescriptor(IIconsKeys.MESSAGE_CLEAR));
		}

		@Override
		public void run() {
			// close the tray
			TrayDialog dialog = (TrayDialog) shell.getData();
			dialog.closeTray();
			// set focus back to shell
			shell.setFocus();
		}

	}

	/** The hint used to compute the width of the tray */
	private final int widthHint;
	/** The browser widget used to display the documentation */
	private Browser browser;
	/** The parent shell of the wizard dialog */
	private Shell shell;
	/** The action invoked to hide the tray */
	private IAction closeAction;
	/** The action invoked to navigate backward in the browser history */
	private IAction backAction;
	/** The action invoked to navigate forward in the browser history */
	private IAction forwardAction;

	/**
	 * Creates the help tray with the given width (this is just a hint).
	 * <p>
	 * The controls are created later in the {@link #createContents(Composite)}
	 * method.
	 * </p>
	 * 
	 * @param widthHint
	 */
	public HelpDialogTray(int widthHint) {
		this.widthHint = widthHint;

	}

	/**
	 * Creates any actions needed by the tray.
	 */
	private void createActions() {
		closeAction = new CloseAction();
		backAction = new BrowserBackAction(browser);
		forwardAction = new BrowserForwardAction(browser);
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		container.setLayout(layout);
		container.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				dispose();
			}
		});

		// Create the toolbar
		final ToolBarManager tbm = new ToolBarManager(SWT.FLAT);
		tbm.createControl(container);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.grabExcessHorizontalSpace = true;
		tbm.getControl().setLayoutData(gd);
		Label separator = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.heightHint = 1;
		separator.setLayoutData(gd);

		try {
			// Create the browser
			browser = new Browser(container, SWT.NONE);
			GridData data = new GridData(GridData.FILL_BOTH);
			data.widthHint = this.widthHint;
			browser.setLayoutData(data);

			// Create the actions
			createActions();
			tbm.add(backAction);
			tbm.add(forwardAction);
			tbm.add(closeAction);
			tbm.update(true);

			// Hook itself as a IPageChangeListener
			shell = parent.getShell();
			hookPageChangeListener(shell);
			browser.addListener(SWT.Dispose, new Listener() {
				public void handleEvent(Event event) {
					unhookPageChangeListener(shell);
				}
			});

			// Remove the browser from the tablist
			List<Control> newTabList = new ArrayList<Control>();
			Control[] oldTabList = container.getTabList();
			for (Control control : oldTabList) {
				if (!(control instanceof Browser)) {
					newTabList.add(control);
				}
			}
			container.setTabList(newTabList.toArray(new Control[0]));

			// Create the toggle browser action
			ToggleBrowserAction tba = new ToggleBrowserAction(shell, browser);
			// Register with the keybinding service
			IHandlerService handlerService = (IHandlerService) PlatformUI
					.getWorkbench().getService(IHandlerService.class);
			handlerService.activateHandler(ICommandConstants.TOGGLE_BROWSER,
					new ActionHandler(tba), new ActiveShellExpression(shell));
		} catch (SWTError e) {
			GuiPlugin.get().error("Couldn't instantiate the browser widget", e); //$NON-NLS-1$
		}

		return container;
	}

	/**
	 * Disposes any resources used by the tray.
	 */
	private void dispose() {
	}

	/**
	 * Add the listener that gets notified of page changes (to automatically
	 * update context help).
	 * 
	 * @param parent
	 *            the Composite to hook the listener to
	 */
	private void hookPageChangeListener(Composite parent) {
		Object data = parent.getData();
		if (data instanceof IPageChangeProvider) {
			((IPageChangeProvider) data).addPageChangedListener(this);
		}
	}

	/**
	 * Called whenever the dialog we're inside has changed pages. This updates
	 * the context help page if it is visible.
	 * 
	 * @param event
	 *            the page change event
	 */
	public void pageChanged(PageChangedEvent event) {
		// Nothing by default
	}

	/**
	 * Sets the internal browser's URL to <code>string</code>.
	 * 
	 * @param string
	 *            The new URL set to the internal browser.
	 */
	public void setUrl(String string) {
		if (browser != null) {
			browser.setUrl(string);
		}
	}

	/**
	 * Remove the listener that gets notified of page changes (to automatically
	 * update context help).
	 * 
	 * @param parent
	 *            the Composite that had the listener
	 */
	private void unhookPageChangeListener(Composite parent) {
		Object data = parent.getData();
		if (data instanceof IPageChangeProvider) {
			((IPageChangeProvider) data).removePageChangedListener(this);
		}
	}
}
