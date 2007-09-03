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
package org.daisy.pipeline.gui.jobs.wizard;

import java.net.URI;

import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.JobsPerspective;
import org.daisy.pipeline.gui.PipelineUtil;
import org.daisy.pipeline.gui.jobs.NewJobOperation;
import org.daisy.pipeline.gui.model.JobManager;
import org.daisy.pipeline.gui.util.actions.OperationUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;

/**
 * The wizard used to create new Pipeline Jobs.
 * <p>
 * This wizard is composed of:
 * </p>
 * <ol>
 * <li>A "Script" page to select the script on which the new job will be based.</li>
 * <li>A "Parameter" page to configure the parameters of the Job to be created.</li>
 * </ol>
 * <p>
 * Note that a contextual help about the selected script is available has a
 * dialog side panel.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public class NewJobWizard extends Wizard implements INewWizard {

	/** The name of the section about this wizard in the dialog settings */
	public static final String SETTINGS_SECTION = "NewJobWizard"; //$NON-NLS-1$
	/**
	 * The name of the setting used to store the session ID. This ID is used to
	 * check if the wizard is used for the first time in the application
	 */
	public static final String SETTINGS_SESSION_ID = "SessionID"; //$NON-NLS-1$
	/** The ID of the wizard */
	public static final String ID = "org.daisy.pipeline.gui.wizard.newJob"; //$NON-NLS-1$
	/** The Job that is created by this wizard */
	private Job job;
	/** Whether this wizard is launched for the first time in the session */
	private boolean isFirstInSession;
	/** The page for script selection */
	private ScriptsWizardPage scriptPage;
	/** The page for script parameters configuration */
	private ParamsWizardPage paramPage;
	/** The workbench used at initialization time */
	private IWorkbench workbench;
	/** The dialog tray used to display the contextual help */
	private HelpDialogTray helpTray;

	/**
	 * Creates this wizard and initialize the dialog settings.
	 */
	public NewJobWizard() {
		initDialogSettings();
		setNeedsProgressMonitor(false);
		setDefaultPageImageDescriptor(GuiPlugin
				.createDescriptor(IIconsKeys.WIZ_NEW_JOB));
	}

	@Override
	public void addPages() {
		super.addPages();
		setWindowTitle(Messages.wizard_title);
		scriptPage = new ScriptsWizardPage();
		addPage(scriptPage);
		paramPage = new ParamsWizardPage();
		addPage(paramPage);

	}

	/**
	 * Creates the script selection page controls only, the parameters
	 * configuration page controls are lazy created.
	 * <p>
	 * Note that the <code>Wizard</code> implementation of this
	 * <code>IWizard</code> method creates all the pages controls using
	 * <code>IDialogPage.createControl</code>
	 * </p>
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		scriptPage.createControl(pageContainer);
		Assert.isNotNull(scriptPage.getControl());
	}

	/**
	 * Returns the Pipeline job created by this wizard.
	 * 
	 * @return the Pipeline job created by this wizard.
	 */
	public Job getJob() {
		return job;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.INewWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}

	/**
	 * Initializes the dialog settings, sets the {@link #isFirstInSession} flag.
	 */
	private void initDialogSettings() {
		// Retrieve the dialog settings
		IDialogSettings dialogSettings = GuiPlugin.get().getDialogSettings();
		IDialogSettings wizardSettings = dialogSettings
				.getSection(SETTINGS_SECTION);
		if (wizardSettings == null) {
			wizardSettings = dialogSettings.addNewSection(SETTINGS_SECTION);
		}
		// Check whether the wizard is invoked for the 1st time in this session
		String sessionID = GuiPlugin.get().getUUID().toString();
		if (!sessionID.equals(wizardSettings.get(SETTINGS_SESSION_ID))) {
			wizardSettings.put(SETTINGS_SESSION_ID, sessionID);
			isFirstInSession = true;
		} else {
			isFirstInSession = false;
		}
		// Set the settings to the dialog
		setDialogSettings(wizardSettings);
	}

	/**
	 * Returns whether this wizard has been launched for the first time in the
	 * application session.
	 * 
	 * @return Whether this wizard has been launched for the first time in the
	 *         application session.
	 */
	public boolean isFirstInSession() {
		return isFirstInSession;
	}

	/**
	 * Adds the created Job to the {@link JobManager} and switches to the the
	 * Job perspective.
	 */
	@Override
	public boolean performFinish() {
		OperationUtil.execute(new NewJobOperation(job), getShell());
		try {
			workbench.showPerspective(JobsPerspective.ID, workbench
					.getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			GuiPlugin.get().error(
					"Couldn't switch to the Documentation perspective", e); //$NON-NLS-1$
		}
		return true;
	}

	/**
	 * Opens the dialog tray an displays the contextual script help. Does
	 * nothing if the tray is already opened.
	 */
	public void performHelp() {
		IWizardContainer container = getContainer();
		if (container instanceof WizardDialog) {
			WizardDialog dialog = (WizardDialog) container;
			if (dialog.getTray() == null) {
				if (helpTray == null) {
					helpTray = new HelpDialogTray((int) (getShell()
							.getClientArea().width * 0.6));
				}
				dialog.openTray(helpTray);
			}
			refreshDoc();
		}
	}

	/**
	 * Refreshes the contextual help page for the current script.
	 */
	private void refreshDoc() {
		if ((((WizardDialog) getContainer()).getTray() == null)
				|| (job == null)) {
			return;
		}
		URI doc = job.getScript().getDocumentation();
		if (doc != null) {
			helpTray.setUrl(doc.toString());
		} else {
			helpTray.setUrl(PipelineUtil.DOC_404.toString());
		}
	}

	/**
	 * Invoked when the user selected a new script from the first page.
	 * 
	 * @param script
	 *            The script newly selected.
	 */
	public void scriptSelected(Script script) {
		if (script == null) {
			job = null;
		} else {
			job = new Job(script);
			paramPage.setPageComplete(false);
		}
		refreshDoc();
	}

	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		super.setContainer(wizardContainer);
		// show the "help" link to the dialog tray
		if ((wizardContainer != null)
				&& (wizardContainer instanceof WizardDialog)) {
			((WizardDialog) wizardContainer).setHelpAvailable(true);
		}
	}

}
