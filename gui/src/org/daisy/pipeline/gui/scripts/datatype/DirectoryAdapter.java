/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.scripts.datatype;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.DirectoryDatatype;
import org.daisy.pipeline.gui.util.Messages;
import org.daisy.pipeline.gui.util.DialogHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Romain Deltour
 * 
 */
public class DirectoryAdapter extends DefaultAdapter {

    @Override
    public Control createControl(final Composite parent, ScriptParameter param,
            int numCol) {
        createLabel(parent, param);
        final DirectoryDatatype type = (DirectoryDatatype) param.getDatatype();
        final Text field = new Text(parent, SWT.SINGLE | SWT.BORDER);
        Button button = new Button(parent, SWT.PUSH | SWT.CENTER);
        button.setText(Messages.button_browse);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                File file = new File(field.getText());
                int style = (type.isInput()) ? SWT.OPEN : SWT.SAVE;
                String path = DialogHelper.browseDir(parent.getShell(), file,
                        style);
                if (path != null) {
                    field.setText(path);
                }
            }
        });
        field.setData(param);
        final GridData data = new GridData();
        data.horizontalSpan = numCol - 2;
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        field.setLayoutData(data);
        return field;
    }

    @Override
    public int getNumCol() {
        return 3;
    }
}
