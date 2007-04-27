package org.daisy.pipeline.gui.jobs.wizard;

import java.net.URI;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Script;
import org.daisy.pipeline.gui.JobsPerspective;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.jobs.NewJobOperation;
import org.daisy.pipeline.gui.util.actions.OperationUtil;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;

public class NewJobWizard extends Wizard implements INewWizard {

    public static final String SETTINGS_SECTION = "NewJobWizard";
    public static final String ID = "org.daisy.pipeline.gui.wizard.newJob";
    private Job job;
    private Browser browser;
    private ScriptsWizardPage scriptPage;
    private ParamsWizardPage paramPage;
    private IWorkbench workbench;

    public NewJobWizard() {
        // Retrieve the dialog settings
        IDialogSettings dialogSettings = GuiPlugin.get()
                .getDialogSettings();
        IDialogSettings wizardSettings = dialogSettings
                .getSection(SETTINGS_SECTION);
        if (wizardSettings == null) {
            wizardSettings = dialogSettings.addNewSection(SETTINGS_SECTION);
        }
        setDialogSettings(dialogSettings);
        setNeedsProgressMonitor(false);
    }

    public void performHelp() {
        IWizardContainer container = getContainer();
        if (container instanceof WizardDialog) {
            WizardDialog dialog = (WizardDialog) container;
            if (dialog.getTray() == null) {
                dialog.openTray(new DialogTray() {
                    @Override
                    protected Control createContents(Composite parent) {
                        Composite control = new Composite(parent, SWT.NONE);
                        control.setLayout(new GridLayout());
                        browser = new Browser(control, SWT.NONE);
                        GridData data = new GridData(GridData.FILL_BOTH);
                        data.widthHint = (int) (getShell().getClientArea().width * 0.6);
                        browser.setLayoutData(data);
                        return control;

                    }
                });
            }
            refreshDoc();
        }
    }

    private void refreshDoc() {
        if (browser == null || job == null) {
            return;
        }
        URI doc = job.getScript().getDocumentation();
        if (doc != null) {
            browser.setUrl(doc.toString());
        } else {
            // TODO set default URL if doc not found
            browser.setUrl("");
        }
    }

    @Override
    public boolean performFinish() {
        OperationUtil.execute(new NewJobOperation(job), getShell());
        try {
            // TODO show dialog before switching to the jobs perspective
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
        // TODO pre-select script from workbench selection
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
}
