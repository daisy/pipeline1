package org.daisy.pipeline.gui.jobs.wizard;

import java.net.URI;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Script;
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

    public static final String SETTINGS_SECTION = "NewJobWizard";
    public static final String SETTINGS_SESSION_ID = "SessionID";
    public static final String ID = "org.daisy.pipeline.gui.wizard.newJob";
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
                helpTray.setFocus();
            }
            refreshDoc();
        }
    }

    private void refreshDoc() {
        if (helpTray == null || job == null) {
            return;
        }
        URI doc = job.getScript().getDocumentation();
        if (doc != null) {
            helpTray.setUrl(doc.toString());
        } else {
            // TODO set default URL if doc not found
            helpTray.setUrl("");
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
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            paramPage.updatePageComplete(false);
        }
        refreshDoc();
    }

    @Override
    public void addPages() {
        super.addPages();
        setWindowTitle("New Job Wizard");
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
