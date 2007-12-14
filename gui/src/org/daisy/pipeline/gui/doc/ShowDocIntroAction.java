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
package org.daisy.pipeline.gui.doc;

import java.util.Map;
import java.util.Properties;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.ICommandConstants;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

/**
 * The action used to switch to the documentation perspective from the welcome
 * screen.
 * 
 * @see IIntroAction
 * @author Romain Deltour
 */
public class ShowDocIntroAction implements IIntroAction {

	/**
	 * Creates a new action.
	 */
	public ShowDocIntroAction() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite,
	 *      java.util.Properties)
	 */
	@SuppressWarnings("restriction")
	public void run(IIntroSite site, Properties params) {
		// IIntroManager introMan = site.getWorkbenchWindow().getWorkbench()
		// .getIntroManager();
		// introMan.closeIntro(introMan.getIntro());
		org.eclipse.ui.internal.intro.impl.model.url.IntroURLParser parser = new org.eclipse.ui.internal.intro.impl.model.url.IntroURLParser(
				"http://org.eclipse.ui.intro/switchToLaunchBar"); //$NON-NLS-1$
		parser.getIntroURL().execute();

		// Execute the show perspective command
		ICommandService cmdServ = (ICommandService) PlatformUI.getWorkbench()
				.getService(ICommandService.class);
		Command cmd = cmdServ.getCommand(ICommandConstants.SHOW_DOC_CMD);
		Map<String, Object> parameters = new java.util.HashMap<String, Object>();
		parameters.put(ICommandConstants.SHOW_DOC_PARAM_WARNING, "" + false);
		IHandlerService handlerServ = (IHandlerService) PlatformUI
				.getWorkbench().getService(IHandlerService.class);
		try {
			// showDocHandler.execute(new ExecutionEvent(null, parameters, null,
			// handlerServ.getCurrentState()));
			cmd.executeWithChecks(new ExecutionEvent(cmd, parameters, null,
					handlerServ.getCurrentState()));
		} catch (CommandException e) {
			GuiPlugin.get().error("Documentation couldn't be opened", e);

		}
	}
}
