package org.daisy.pipeline.gui.jobs.wizard;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.daisy.pipeline.gui.scripts.ScriptHelper;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewJobWizard extends Wizard implements INewWizard {

    public static final String SETTINGS_SECTION = "NewJobWizard";
    private ScriptHandler script;

    public NewJobWizard() {
        // Retrieve the dialog settings
        IDialogSettings dialogSettings = PipelineGuiPlugin.getDefault()
                .getDialogSettings();
        IDialogSettings wizardSettings = dialogSettings
                .getSection(SETTINGS_SECTION);
        if (wizardSettings == null) {
            wizardSettings = dialogSettings.addNewSection(SETTINGS_SECTION);
        }
        setDialogSettings(dialogSettings);
    }

    @Override
    public boolean performFinish() {
        System.out.println("new job created");
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO When scripts view is added: set the selected script to the given
        // initial selection.
    }

    public void setScript(ScriptHandler ascript) {
        script = ascript;
        updateWizard();
    }

    private void updateWizard() {
        if (ScriptHelper.isOutputRequired(script)) {
            addPage(new SelectDestWizardPage());
        }
    }

    @Override
    public void addPages() {
        super.addPages();
        setWindowTitle("New Job Wizard");
        addPage(new SelectConvWizardPage());
        addPage(new SelectSourceWizardPage());
    }

}
