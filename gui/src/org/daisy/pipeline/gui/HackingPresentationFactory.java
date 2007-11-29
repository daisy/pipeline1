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
package org.daisy.pipeline.gui;

import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.internal.presentations.r33.WorkbenchPresentationFactory_33;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;

/**
 * A presentation factory hooking the default presentation factory to add an
 * accessible name to view drop-down buttons.
 * 
 * @author Romain Deltour
 * 
 */
@SuppressWarnings("restriction") //$NON-NLS-1$
public class HackingPresentationFactory extends WorkbenchPresentationFactory_33 {

	@Override
	public StackPresentation createViewPresentation(Composite parent,
			IStackPresentationSite site) {
		StackPresentation sp = super.createViewPresentation(parent, site);
		for (Control control : ((Composite) sp.getControl()).getChildren()) {
			if (control instanceof ToolBar) {
				control.getAccessible().addAccessibleListener(
						new AccessibleAdapter() {

							@Override
							public void getName(AccessibleEvent e) {
								e.result = Messages.accessibleName_viewMenu;
							}

						});
			}
		}
		return sp;
	}

}
