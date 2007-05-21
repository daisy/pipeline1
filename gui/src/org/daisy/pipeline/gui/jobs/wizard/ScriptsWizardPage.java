package org.daisy.pipeline.gui.jobs.wizard;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Script;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.PipelineUtil;
import org.daisy.pipeline.gui.scripts.ScriptFileFilter;
import org.daisy.pipeline.gui.scripts.ScriptManager;
import org.daisy.pipeline.gui.scripts.ScriptsLabelProvider;
import org.daisy.pipeline.gui.util.viewers.ExpandTreeDoubleClickListener;
import org.daisy.pipeline.gui.util.viewers.FileTreeContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class ScriptsWizardPage extends WizardPage {

    public static final String NAME = "selectScript";
    public static final String SETTINGS_LAST_SCRIPT_URI = "lastScriptURI";

    private ScriptManager scriptMan;
    private IStructuredSelection selection;
    private TreeViewer scriptTreeViewer;

    protected ScriptsWizardPage() {
        super(NAME);
        setTitle("Select Script");
        setDescription("Select the script the new job will be based on.");
        scriptMan = ScriptManager.getDefault();
    }

    public void createControl(Composite parent) {
        // Tree of script files
        scriptTreeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.BORDER);
        scriptTreeViewer.getTree().setLayoutData(
                new GridData(GridData.FILL_BOTH));
        scriptTreeViewer.setContentProvider(new FileTreeContentProvider(
                new ScriptFileFilter(true)));
        scriptTreeViewer.setLabelProvider(new ScriptsLabelProvider());
        scriptTreeViewer.setInput(PipelineUtil.getScriptDir());
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
        // Go to next page when double-clicking a script
        scriptTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                if (isPageComplete()) {
                    IWizardPage page = getNextPage();
                    if (page != null) {
                        getContainer().showPage(page);
                    }
                }
            }
        });
        scriptTreeViewer
                .addDoubleClickListener(new ExpandTreeDoubleClickListener());
        setControl(scriptTreeViewer.getControl());
        initContent();
    }

    private void initContent() {
        if (!((NewJobWizard) getWizard()).isFirstInSession()) {
            String lastScriptURI = getDialogSettings().get(
                    SETTINGS_LAST_SCRIPT_URI);
            if (lastScriptURI != null) {
                File scriptFile;
                try {
                    scriptFile = new File(new URI(lastScriptURI));
                    scriptTreeViewer.setSelection(new StructuredSelection(
                            scriptFile), true);
                } catch (URISyntaxException e) {
                    GuiPlugin.get().error(
                            "Couldn't create script URI from wizard settings",
                            e);
                }
            }
        }
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
            return;
        }
        Script script = scriptMan.getScript(file.toURI());
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

        // Store the script URI in wizard settings
        getDialogSettings().put(SETTINGS_LAST_SCRIPT_URI,
                file.toURI().toString());

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
