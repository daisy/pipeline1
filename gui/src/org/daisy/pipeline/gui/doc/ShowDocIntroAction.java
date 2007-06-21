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

import java.util.Properties;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

/**
 * @author Romain Deltour
 * 
 */
public class ShowDocIntroAction implements IIntroAction {

    private IAction showDocAction;

    public ShowDocIntroAction() {
        showDocAction = new ShowDocAction();
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
                "http://org.eclipse.ui.intro/switchToLaunchBar");
        parser.getIntroURL().execute();
        showDocAction.run();
    }

}
