package org.daisy.pipeline.gui.scripts.datatype;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.FilesDatatype;
import org.daisy.pipeline.gui.util.DialogHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class FilesAdapter extends DefaultAdapter {

    @Override
    public Control createControl(final Composite parent, ScriptParameter param,
            int numCol) {
        final FilesDatatype type = (FilesDatatype) param.getDatatype();
        final Text field = new Text(parent, SWT.SINGLE | SWT.BORDER);
        Button button = new Button(parent, SWT.PUSH | SWT.CENTER);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                File file = new File(field.getText());
                int style = (type.isInput()) ? SWT.OPEN : SWT.SAVE;
                String path = DialogHelper.browseFile(parent.getShell(), file,
                        SWT.MULTI | style, type.getMime());
                if (path != null) {
                    field.setText(path);
                }
            }
        });
        field.setData(param);
        final GridData data = new GridData();
        data.horizontalSpan = numCol - 1;
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        field.setLayoutData(data);
        return field;
    }

    @Override
    public int getNumCol() {
        return 2;
    }

}