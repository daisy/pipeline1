package org.daisy.pipeline.gui.jobs.wizard;

import java.io.File;

import org.daisy.pipeline.gui.util.DialogHelper;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SelectDestWizardPage extends WizardPage {

    public static final String NAME = "selectDest";
    private Text sourceFileField;

    protected SelectDestWizardPage() {
        super(NAME);
        setTitle("Select Destination");
        setDescription("Select a destination file for the new job.");
    }

    public void createControl(Composite parent) {
        // Container composite with 2 columns
        Composite container = new Composite(parent, SWT.NULL);
        final GridLayout containerLayout = new GridLayout(3, false);
        containerLayout.marginLeft = 20;
        containerLayout.marginRight = 20;
        container.setLayout(containerLayout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        setControl(container);

        // Source Field Label
        final Label sourceLabel = new Label(container, SWT.NONE);
        sourceLabel.setText("Destination File:");

        // Source Field
        sourceFileField = new Text(container, SWT.BORDER);
        sourceFileField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                updatePageComplete();
            }
        });
        final GridData sourceFieldData = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_CENTER
                | GridData.FILL_HORIZONTAL);
        sourceFileField.setLayoutData(sourceFieldData);

        // Browse button
        final Button browseButton = new Button(container, SWT.NONE);
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browse();
            }
        });
        browseButton.setText("Browse...");

        // Init the page contents
        initContents();
    }

    private void initContents() {
        setPageComplete(false);
        setMessage(null);
        setMessage(null);
    }

    private void browse() {
        File file = new File(sourceFileField.getText());
        String path = DialogHelper.browseFile(getShell(), file, SWT.CLOSE, null);
        if (path!=null) {
            sourceFileField.setText(path);
        }
    }

    private void updatePageComplete() {
        setPageComplete(false);
        String text = sourceFileField.getText();
        if (text == null || text.length() == 0) {
            setMessage(null);
            setErrorMessage("Please select a destination file.");
            return;
        }
        File file = new File(text);
        if (!file.exists()) {
            setMessage(null);
            setErrorMessage("The selected file does not exist.");
            return;
        }
        // TODO check mime type
        setPageComplete(true);
        // Reset messages
        setMessage(null);
        setErrorMessage(null);
    }

}
