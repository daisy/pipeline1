package org.daisy.pipeline.gui.jobs.wizard;

import java.io.File;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Script;
import org.daisy.pipeline.gui.scripts.ScriptFileFilter;
import org.daisy.pipeline.gui.scripts.ScriptManager;
import org.daisy.pipeline.gui.scripts.ScriptsLabelProvider;
import org.daisy.pipeline.gui.util.jface.FileTreeContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class ScriptsWizardPage extends WizardPage {

    public static final String NAME = "selectScript";
    private ScriptManager scriptMan;
    private IStructuredSelection selection;

    protected ScriptsWizardPage() {
        super(NAME);
        setTitle("Select Script");
        setDescription("Select the script the new job will be based on.");
        scriptMan = ScriptManager.getDefault();
    }

    public void createControl(Composite parent) {
        // Tree of script files
        TreeViewer scriptTreeViewer = new TreeViewer(parent, SWT.SINGLE
                | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        scriptTreeViewer.getTree().setLayoutData(
                new GridData(GridData.FILL_BOTH));
        scriptTreeViewer.setContentProvider(new FileTreeContentProvider(
                new ScriptFileFilter()));
        scriptTreeViewer.setLabelProvider(new ScriptsLabelProvider());
        scriptTreeViewer.setInput(scriptMan.getScriptDir());
        scriptTreeViewer.getTree().deselectAll();
        scriptTreeViewer
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        if (event.getSelection() instanceof IStructuredSelection
                                && event.getSelection() != null) {
                            selection = (IStructuredSelection) event
                                    .getSelection();
                            updatePageComplete(true);
                        }
                    }
                });
        setControl(scriptTreeViewer.getControl());
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            // TODO initialize from selection
            updatePageComplete(false);
        }
        super.setVisible(visible);
    }

    void updatePageComplete(boolean showError) {
        ((NewJobWizard) getWizard()).scriptSelected(null);
        setPageComplete(false);

        // Check selection
        File file = (selection == null) ? null : (File) selection
                .getFirstElement();
        if (file == null || file.isDirectory()) {
            setMessage(null);
            setErrorMessage((showError) ? "Please select a script file." : null);
            return;
        }
        Script script = scriptMan.getScript(file.getPath());
        if (script == null) {
            setMessage(null);
            setErrorMessage((showError) ? "Unhandled script file." : null);
            setErrorMessage("Unhandled script file.");
            return;
        }

        // Update script description
        setErrorMessage(null);
        String description = script.getDescription();
        if (description != null && description.length() > 0) {
            setMessage(description, INFORMATION);
        }

        // Send script to the wizard
        ((NewJobWizard) getWizard()).scriptSelected(script);
        setPageComplete(true);
    }

    @Override
    public boolean canFlipToNextPage() {
        Job job = ((NewJobWizard) getWizard()).getJob();
        return (job != null && !job.getScript().getParameters().isEmpty());
    }

    @Override
    public void performHelp() {
        ((NewJobWizard) getWizard()).performHelp();
    }

}
