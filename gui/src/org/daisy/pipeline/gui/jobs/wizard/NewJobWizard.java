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
import org.daisy.pipeline.gui.jobs.NewJobOperation;
import org.daisy.pipeline.gui.util.actions.OperationUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;

public class NewJobWizard extends Wizard implements INewWizard {

    public static final String SETTINGS_SECTION = "NewJobWizard"; //$NON-NLS-1$
    public static final String SETTINGS_SESSION_ID = "SessionID"; //$NON-NLS-1$
    public static final String ID = "org.daisy.pipeline.gui.wizard.newJob"; //$NON-NLS-1$
    private Job job;
    private boolean isFirstInSession;
    private ScriptsWizardPage scriptPage;
    private ParamsWizardPage paramPage;
    private IWorkbench workbench;
    private HelpDialogTray helpTray;

    public NewJobWizard() {
        initDialogSettings();
        setNeedsProgressMonitor(false);
        setDefaultPageImageDescriptor(GuiPlugin
                .createDescriptor(IIconsKeys.WIZ_NEW_JOB));
    }

    /**
     * 
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

    private void refreshDoc() {
        if (((WizardDialog) getContainer()).getTray() == null || job == null) {
            return;
        }
        URI doc = job.getScript().getDocumentation();
        if (doc != null) {
            helpTray.setUrl(doc.toString());
        } else {
            // TODO set default URL if doc not found
            helpTray.setUrl(""); //$NON-NLS-1$
        }
    }

    @Override
    public boolean performFinish() {
        OperationUtil.execute(new NewJobOperation(job), getShell());
        try {
            // TODO show dialog before switching to the jobInfos perspective
            workbench.showPerspective(JobsPerspective.ID, workbench
                    .getActiveWorkbenchWindow());
        } catch (WorkbenchException e) {
            GuiPlugin.get().error(
                    "Couldn't switch to the Documentation perspective", e);
        }
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
    }

    public boolean isFirstInSession() {
        return isFirstInSession;
    }

    public Job getJob() {
        return job;
    }

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
    public void addPages() {
        super.addPages();
        setWindowTitle(Messages.wizard_title);
        scriptPage = new ScriptsWizardPage();
        addPage(scriptPage);
        paramPage = new ParamsWizardPage();
        addPage(paramPage);

    }

    @Override
    public void setContainer(IWizardContainer wizardContainer) {
        super.setContainer(wizardContainer);
        if (wizardContainer != null && wizardContainer instanceof WizardDialog) {
            ((WizardDialog) wizardContainer).setHelpAvailable(true);
        }
    }

    @Override
    public void createPageControls(Composite pageContainer) {
        // We only pre-create the script selection page controls
        // Paramaeters configuration page controls are lazy created
        scriptPage.createControl(pageContainer);
        Assert.isNotNull(scriptPage.getControl());
    }

}
