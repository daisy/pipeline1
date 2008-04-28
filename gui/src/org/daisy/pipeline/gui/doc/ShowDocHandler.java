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

import java.util.Map;

import org.daisy.pipeline.gui.DocPerspective;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.ICommandConstants;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Command handler for the show doc action.
 * 
 * @author Romain Deltour
 * 
 */
public class ShowDocHandler extends AbstractHandler {
	private static final String dontShowDocSwitchWarningKey = "dontShowDocSwitchWarningKey"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		// Return if we're already in the doc perspective
		IPerspectiveDescriptor currentPersp = window.getActivePage()
				.getPerspective();
		if ((currentPersp != null)
				&& currentPersp.getId().equals(DocPerspective.ID)) {
			return null;
		}
		// Get the parameter whether to handle warning message
		final Map<?, ?> parameters = event.getParameters();
		boolean handleWarning = Boolean.parseBoolean((String) parameters
				.get(ICommandConstants.SHOW_DOC_PARAM_WARNING));

		// Show an informative (toggle) dialog
		if (handleWarning) {
			IPreferenceStore prefStore = GuiPlugin.get().getPreferenceStore();
			boolean skipWarning = prefStore.getString(
					dontShowDocSwitchWarningKey).equals(
					MessageDialogWithToggle.ALWAYS);
			if (!skipWarning) {
				MessageDialogWithToggle.openInformation(window.getShell(),
						Messages.dialog_showDoc_title,
						Messages.dialog_showDoc_message,
						Messages.dialog_showDoc_toggle, false, prefStore,
						dontShowDocSwitchWarningKey);
			}
		}

		// Do go to the Documentation perspective
		try {
			window.getWorkbench().showPerspective(DocPerspective.ID, window);
		} catch (WorkbenchException e) {
			throw new ExecutionException("Perspective could not be opened.", e);
		}
		return null;
	}
}
