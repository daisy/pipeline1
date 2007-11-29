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
package org.daisy.pipeline.gui.update;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * The wizard used to apply a software update patch to the Pipeline.
 * <p>
 * This wizard is composed of a single page asking the user for a zipped updated
 * patch to apply.
 * </p>
 * 
 * @author Romain Deltour
 */
public class UpdateWizard extends Wizard implements IWorkbenchWizard {
	ZipUpdateWizardPage zipUpdatePage;

	/**
	 * Creates this wizard.
	 */
	public UpdateWizard() {
		setDefaultPageImageDescriptor(GuiPlugin
				.createDescriptor(IIconsKeys.WIZ_UPDATE));
	}

	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle(Messages.wizard_title);
		zipUpdatePage = new ZipUpdateWizardPage();
		addPage(zipUpdatePage);
	}

	/**
	 * Initializes the wizard using the passed workbench and object selection.
	 * This implementation does nothing.
	 * <p>
	 * This method is called after the no argument constructor and before other
	 * methods are called.
	 * </p>
	 * 
	 * @param workbench
	 *            the current workbench
	 * @param selection
	 *            the current object selection
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		return zipUpdatePage.finish();
	}
}
