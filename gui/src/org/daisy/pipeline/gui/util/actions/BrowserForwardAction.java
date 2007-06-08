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
package org.daisy.pipeline.gui.util.actions;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.util.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.ui.ISharedImages;

/**
 * @author Romain Deltour
 * 
 */
public class BrowserForwardAction extends Action {
    private Browser browser;

    public BrowserForwardAction(Browser browser) {
        super();
        this.browser = browser;
        setImageDescriptor(GuiPlugin
                .getSharedDescriptor(ISharedImages.IMG_TOOL_FORWARD));
        setDisabledImageDescriptor(GuiPlugin
                .getSharedDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));
        setText(Messages.action_forward);
        setToolTipText(Messages.action_forward_tooltip);
        setEnabled(browser.isForwardEnabled());
        this.browser.addLocationListener(new LocationAdapter() {
            @Override
            public void changed(LocationEvent event) {
                setEnabled(BrowserForwardAction.this.browser.isForwardEnabled());
            }
        });
    }

    @Override
    public void run() {
        browser.forward();
    }
}