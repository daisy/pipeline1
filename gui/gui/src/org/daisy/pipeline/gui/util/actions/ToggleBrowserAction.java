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
package org.daisy.pipeline.gui.util.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Action used to toggle the focus in/out a Browser widget.
 * 
 * @author Romain Deltour
 * 
 */
public class ToggleBrowserAction extends Action {
	/** The shell hosting the Browser widget */
	private Shell shell;
	/** The Browser widget we toggle the focus in/out */
	private Browser browser;
	/** The Control that previously had focus before using the action */
	private Control lastFocusOwner;

	/**
	 * Creates an instance of this action for the given shell and the given
	 * browser.
	 * 
	 * @param shell
	 *            a Shell that will get the focus in last resort
	 * @param browser
	 *            The Browser widget we want the focus to move in/out
	 */
	public ToggleBrowserAction(Shell shell, Browser browser) {
		super();
		this.browser = browser;
		this.shell = shell;
	}

	@Override
	public void run() {
		if (browser.isDisposed()) {
			return;
		}
		if (!browser.isFocusControl()) {
			Control control = shell.getDisplay().getFocusControl();
			if (browser.forceFocus()) {
				lastFocusOwner = control;
				// an additional TAB is required to focus the browser content:
				browser.traverse(SWT.TRAVERSE_TAB_NEXT);
			}
		} else {
			if ((lastFocusOwner != null) && !lastFocusOwner.isDisposed()) {
				lastFocusOwner.forceFocus();
			} else {
				shell.setFocus();
			}
			lastFocusOwner = null;
		}
	}

}