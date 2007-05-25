/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.jobs.wizard;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.util.actions.BrowserBackAction;
import org.daisy.pipeline.gui.util.actions.BrowserForwardAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
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

/**
 * @author Romain Deltour
 * 
 */
public class HelpDialogTray extends DialogTray implements IPageChangedListener {

    private final int widthHint;

    private Browser browser;
    private Shell shell;
    private IAction closeAction;
    private IAction backAction;
    private IAction forwardAction;

    public HelpDialogTray(int widthHint) {
        this.widthHint = widthHint;

    }

    /**
     * Called whenever the dialog we're inside has changed pages. This updates
     * the context help page if it is visible.
     * 
     * @param event the page change event
     */
    public void pageChanged(PageChangedEvent event) {
        // Nothing by default
    }

    public void setUrl(String string) {
    	if (browser != null) {
    		browser.setUrl(string);
    	}
    }

    /**
     * Creates any actions needed by the tray.
     */
    private void createActions() {
        closeAction = new CloseAction();
        backAction = new BrowserBackAction(browser);
        forwardAction = new BrowserForwardAction(browser);
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
     * @param parent the Composite to hook the listener to
     */
    private void hookPageChangeListener(Composite parent) {
        Object data = parent.getData();
        if (data instanceof IPageChangeProvider) {
            ((IPageChangeProvider) data).addPageChangedListener(this);
        }
    }

    /**
     * Remove the listener that gets notified of page changes (to automatically
     * update context help).
     * 
     * @param parent the Composite that had the listener
     */
    private void unhookPageChangeListener(Composite parent) {
        Object data = parent.getData();
        if (data instanceof IPageChangeProvider) {
            ((IPageChangeProvider) data).removePageChangedListener(this);
        }
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
        } catch (SWTError e) {
        	GuiPlugin.get().error("Couldn't instantiate browser widget", e); //$NON-NLS-1$
        }

        return container;
    }

    private class CloseAction extends Action {

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

    public void setFocus() {
        // browser.addFocusListener(new FocusListener() {
        //
        // public void focusGained(FocusEvent e) {
        // System.out.println("browser gained focus");
        // }
        //
        // public void focusLost(FocusEvent e) {
        // System.out.println("browser lost focus");
        // }
        //
        // });
        // browser.forceFocus();
    }
}
