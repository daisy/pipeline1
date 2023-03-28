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
package org.daisy.pipeline.gui.doc;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * The Action used to synchronize the current ToC with the doc page currently
 * displayed in the documentation view.
 * 
 * @author Romain Deltour
 * 
 */
public class SyncTocAction extends Action implements IAction {

	private DocView view;

	/**
	 * Creates this action for the given doc view.
	 * 
	 * @param view
	 *            a reference to the doc view
	 */
	public SyncTocAction(DocView view) {
		super(Messages.action_synchronize, IAction.AS_CHECK_BOX);
		setText(Messages.action_synchronize);
		setToolTipText(Messages.action_synchronize_tooltip);
		setImageDescriptor(GuiPlugin
				.createDescriptor(IIconsKeys.HELP_SYNCHRONIZE));
		this.view = view;
		setChecked(view.shouldSynchronizeToc());
	}

	@Override
	public void run() {
		view.setTocSynchronization(isChecked());
	}

}
