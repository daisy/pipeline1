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

import org.daisy.pipeline.gui.DocPerspective;
import org.daisy.pipeline.gui.GuiPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * @author Romain Deltour
 * 
 */
public class ShowDocAction extends Action implements IWorkbenchAction {
    IWorkbenchWindow window;

    public ShowDocAction() {
        this(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
    }

    /**
     * @param window
     */
    public ShowDocAction(IWorkbenchWindow window) {
        super();
        if (window == null) {
            throw new IllegalArgumentException();
        }
        this.window = window;
    }

    @Override
    public void run() {
        if (window == null) {
            return;// the action has been disposed
        }
        try {
            window.getWorkbench().showPerspective(DocPerspective.ID, window);
        } catch (WorkbenchException e) {
            GuiPlugin.get().error(
                    "Couldn't switch to the Documentation perspective", e); //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
     */
    public void dispose() {
        window = null;
    }

}
