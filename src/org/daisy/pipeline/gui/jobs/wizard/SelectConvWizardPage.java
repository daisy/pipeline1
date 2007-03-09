package org.daisy.pipeline.gui.jobs.wizard;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.pipeline.gui.scripts.ScriptManager;
import org.daisy.pipeline.gui.scripts.ScriptsLabelProvider;
import org.daisy.pipeline.gui.util.FileTreeContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SelectConvWizardPage extends WizardPage {

    public static final String NAME = "selectConv";
    private ScriptManager scriptMan;
    private Text descriptionText;
    private IStructuredSelection selection;

    protected SelectConvWizardPage() {
        super(NAME);
        setTitle("Select Conversion");
        setDescription("Select a conversion script for the new job.");
        scriptMan = ScriptManager.getDefault();
    }

    public void createControl(Composite parent) {
        // Container composite with 2 columns
        Composite container = new Composite(parent, SWT.NULL);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout(2, false));
        setControl(container);

        // Tree of script files
        TreeViewer scriptTreeViewer = new TreeViewer(container, SWT.SINGLE
                | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan = 1;
        data.heightHint = 180;
        data.widthHint = 180;
        scriptTreeViewer.getTree().setLayoutData(data);
        scriptTreeViewer.setContentProvider(new FileTreeContentProvider(
                scriptMan.getScriptDir()));
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
                            updatePageComplete();
                        }
                    }
                });

        // Container for the 2nd column: label+description
        Composite descriptionComp = new Composite(container, SWT.NONE);
        descriptionComp.setLayout(new GridLayout(1, false));
        // Label of the text area
        Label descriptionLabel = new Label(descriptionComp, SWT.NONE);
        descriptionLabel.setText("Converter Description");
        // Descriptive text are
        final GridData textData = new GridData(GridData.GRAB_VERTICAL);
        textData.widthHint = 200;
        textData.heightHint = 210;
        descriptionText = new Text(descriptionComp, SWT.MULTI | SWT.BORDER
                | SWT.WRAP | SWT.V_SCROLL);
        descriptionText.setLayoutData(textData);
        descriptionText.setEditable(false);

        // Init the page contents
        initContents();
    }

    private void initContents() {
        if (selection != null) {
            updatePageComplete();
        } else {
            setPageComplete(false);
        }
        setMessage(null);
        setMessage(null);
    }

    private void updatePageComplete() {
        setPageComplete(false);
        File file = (File) selection.getFirstElement();
        if (file == null || file.isDirectory()) {
            setMessage(null);
            setErrorMessage("Please select a script file.");
            return;
        }
        ScriptHandler script = scriptMan.getScript(file.getPath());
        if (script == null) {
            setMessage(null);
            setErrorMessage("Unhandled script file. Please select another file.");
            return;
        }
        setPageComplete(true);
        // Update script description
        String description = script.getDescription();
        descriptionText.setText(description != null ? description : "");
        // Send script to the wizard
        ((NewJobWizard) getWizard()).setScript(script);
        // Reset messages
        setMessage(null);
        setErrorMessage(null);
    }

    @Override
    public IWizardPage getNextPage() {
        // TODO Auto-generated method stub
        return super.getNextPage();
    }

}
