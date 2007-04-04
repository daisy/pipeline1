package org.daisy.pipeline.gui.jobs.wizard;

import java.io.File;
import java.net.URL;

import org.daisy.dmfc.core.script.Script;
import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.daisy.pipeline.gui.scripts.ScriptFileFilter;
import org.daisy.pipeline.gui.scripts.ScriptManager;
import org.daisy.pipeline.gui.scripts.ScriptsLabelProvider;
import org.daisy.pipeline.gui.util.jface.FileTreeContentProvider;
import org.eclipse.jface.dialogs.DialogTray;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class SelectScriptWizardPage extends WizardPage {

    public static final String NAME = "selectScript";
    private ScriptManager scriptMan;
    private Text descriptionText;
    private IStructuredSelection selection;

    protected SelectScriptWizardPage() {
        super(NAME);
        setTitle("Select Conversion");
        setDescription("Select a conversion script for the new job.");
        scriptMan = ScriptManager.getDefault();
    }

    public void createControl(Composite parent) {
        // Container: SashForm with two Groups (script tree & description)
        SashForm container = new SashForm(parent, SWT.HORIZONTAL);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        Group scriptGroup = new Group(container, SWT.SHADOW_NONE);
        scriptGroup.setLayout(layout);
        scriptGroup.setText("Conversion Scripts");
        Group descrGroup = new Group(container, SWT.SHADOW_NONE);
        descrGroup.setLayout(layout);
        descrGroup.setText("Description");
        container.setWeights(new int[] { 2, 1 });
        setControl(container);

        // Tree of script files
        TreeViewer scriptTreeViewer = new TreeViewer(scriptGroup, SWT.SINGLE
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
                            updatePageComplete();
                        }
                    }
                });

        // Descriptive text are
        descriptionText = new Text(descrGroup, SWT.MULTI | SWT.BORDER
                | SWT.WRAP | SWT.V_SCROLL);
        descriptionText.setLayoutData(new GridData(GridData.FILL_BOTH));
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
        Script script = scriptMan.getScript(file.getPath());
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

    @Override
    public void performHelp() {
        IWizardContainer container = getContainer();
        if (container instanceof WizardDialog) {
            WizardDialog dialog = (WizardDialog) container;
            dialog.openTray(new DialogTray() {
                @Override
                protected Control createContents(Composite parent) {
                    Composite control = new Composite(parent, SWT.NONE);
                    control.setLayout(new GridLayout(1, true));
                    GridData data = new GridData(GridData.FILL_BOTH);
                    data.widthHint = (int) (getShell().getClientArea().width * 0.6);
                    Browser browser = new Browser(control, SWT.NONE);
                    browser.setLayoutData(new GridData(GridData.FILL_BOTH));
                    URL url = PipelineGuiPlugin.getResourceURL("./index.html");
                    browser.setUrl(url.toString());
                    browser.setLayoutData(data);
                    return control;

                }
            });
        }
    }

}
