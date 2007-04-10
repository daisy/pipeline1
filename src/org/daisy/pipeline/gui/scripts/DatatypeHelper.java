package org.daisy.pipeline.gui.scripts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.BooleanDatatype;
import org.daisy.dmfc.core.script.datatype.Datatype;
import org.daisy.dmfc.core.script.datatype.DirectoryDatatype;
import org.daisy.dmfc.core.script.datatype.EnumDatatype;
import org.daisy.dmfc.core.script.datatype.EnumItem;
import org.daisy.dmfc.core.script.datatype.FileDatatype;
import org.daisy.dmfc.core.script.datatype.IntegerDatatype;
import org.daisy.pipeline.gui.util.DialogHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Romain Deltour
 * 
 */
public final class DatatypeHelper {
    private DatatypeHelper() {
        // Nothing (static helper)
    }

    public static int getNumColumns(Datatype type) {
        if (type == null) {
            // TODO log error
            return 1;
        }
        switch (type.getType()) {
        case BOOLEAN:
            return 1;
        case DIRECTORY:
            return 2;
        case ENUM:
            return 1;
        case FILE:
            return 2;
        case INTEGER:
            return 1;
        case STRING:
            return 1;
        default:
            return 0;
        }
    }

    public static List<Control> createControl(final Composite parent,
            ScriptParameter param, Listener listener, int numCol) {
        List<Control> controls = new ArrayList<Control>();
        Datatype type = param.getDatatype();
        if (type == null) {
            // TODO log error
            System.err.println("null datatype for param" + param.getName());
            Composite empty = new Composite(parent, SWT.NONE);
            final GridData data = new GridData();
            data.horizontalSpan = numCol;
            data.horizontalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            empty.setLayoutData(data);
            controls.add(empty);
            return controls;
        }
        switch (type.getType()) {
        case BOOLEAN:
            BooleanDatatype bType = (BooleanDatatype) type;
            Combo bCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
            bCombo.add(bType.getTrueValue());
            bCombo.add(bType.getFalseValue());
            bCombo.addListener(SWT.Modify, listener);
            bCombo.addListener(SWT.FocusIn, listener);
            bCombo.addListener(SWT.FocusOut, listener);
            bCombo.setData(param);
            final GridData bData = new GridData();
            bData.horizontalSpan = numCol;
            bData.horizontalAlignment = GridData.FILL;
            bData.grabExcessHorizontalSpace = true;
            bCombo.setLayoutData(bData);
            controls.add(bCombo);
            break;
        case DIRECTORY:
            final DirectoryDatatype dType = (DirectoryDatatype) type;
            final Text dField = new Text(parent, SWT.SINGLE | SWT.BORDER);
            Button dButton = new Button(parent, SWT.PUSH | SWT.CENTER);
            dButton.setText("Browse...");
            dButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    File file = new File(dField.getText());
                    int style = (dType.isInput()) ? SWT.OPEN : SWT.SAVE;
                    String path = DialogHelper.browseDir(parent.getShell(),
                            file, style);
                    if (path != null) {
                        dField.setText(path);
                    }
                }
            });
            dField.addListener(SWT.Modify, listener);
            dField.addListener(SWT.FocusIn, listener);
            dField.addListener(SWT.FocusOut, listener);
            dField.setData(param);
            final GridData dfData = new GridData();
            dfData.horizontalSpan = numCol - 1;
            dfData.horizontalAlignment = GridData.FILL;
            dfData.grabExcessHorizontalSpace = true;
            dField.setLayoutData(dfData);
            controls.add(dField);
            controls.add(dButton);
            break;
        case ENUM:
            EnumDatatype eType = (EnumDatatype) type;
            Combo eCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
            for (EnumItem item : eType.getItems()) {
                eCombo.add(item.getNiceName());
            }
            eCombo.addListener(SWT.Modify, listener);
            eCombo.addListener(SWT.FocusIn, listener);
            eCombo.addListener(SWT.FocusOut, listener);
            eCombo.setData(param);
            final GridData eData = new GridData();
            eData.horizontalSpan = numCol;
            eData.horizontalAlignment = GridData.FILL;
            eData.grabExcessHorizontalSpace = true;
            eCombo.setLayoutData(eData);
            controls.add(eCombo);
            break;
        case FILE:
            final FileDatatype fType = (FileDatatype) type;
            final Text fField = new Text(parent, SWT.SINGLE | SWT.BORDER);
            Button fButton = new Button(parent, SWT.PUSH | SWT.CENTER);
            fButton.setText("Browse...");
            fButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    File file = new File(fField.getText());
                    int style = (fType.isInput()) ? SWT.OPEN : SWT.SAVE;
                    String path = DialogHelper.browseFile(parent.getShell(),
                            file, SWT.SINGLE | style, fType.getMime());
                    if (path != null) {
                        fField.setText(path);
                    }
                }
            });
            fField.addListener(SWT.Modify, listener);
            fField.addListener(SWT.FocusIn, listener);
            fField.addListener(SWT.FocusOut, listener);
            fField.setData(param);
            final GridData ffData = new GridData();
            ffData.horizontalSpan = numCol - 1;
            ffData.horizontalAlignment = GridData.FILL;
            ffData.grabExcessHorizontalSpace = true;
            fField.setLayoutData(ffData);
            controls.add(fField);
            controls.add(fButton);
            break;
        case INTEGER:
            IntegerDatatype iType = (IntegerDatatype) type;
            Spinner iSpin = new Spinner(parent, SWT.NONE);
            iSpin.setMinimum(iType.getMin());
            iSpin.setMaximum(iType.getMax());
            iSpin.addListener(SWT.Modify, listener);
            iSpin.addListener(SWT.FocusIn, listener);
            iSpin.addListener(SWT.FocusOut, listener);
            iSpin.setData(param);
            // iSpin.setLayoutData(new FormData());
            final GridData iData = new GridData();
            iData.horizontalSpan = numCol;
            iData.horizontalAlignment = GridData.FILL;
            iData.grabExcessHorizontalSpace = true;
            iSpin.setLayoutData(iData);
            controls.add(iSpin);
            break;
        case STRING:
            Text sField = new Text(parent, SWT.SINGLE | SWT.BORDER);
            sField.addListener(SWT.Modify, listener);
            sField.addListener(SWT.FocusIn, listener);
            sField.addListener(SWT.FocusOut, listener);
            sField.setData(param);
            final GridData sData = new GridData();
            sData.horizontalSpan = numCol;
            sData.horizontalAlignment = GridData.FILL;
            sData.grabExcessHorizontalSpace = true;
            sField.setLayoutData(sData);
            controls.add(sField);
            break;

        default:
            // shouldn't get here
            break;
        }
        return controls;
    }

    public static String getValue(Widget widget, ScriptParameter param) {
        Datatype type = param.getDatatype();
        String value;
        switch (type.getType()) {
        case BOOLEAN:
            value = ((Combo) widget).getText();
            break;
        case DIRECTORY:
            value = ((Text) widget).getText();
            break;
        case ENUM:
            value = ((Combo) widget).getText();
            break;
        case FILE:
            value = ((Text) widget).getText();
            break;
        case INTEGER:
            value = Integer.toString(((Spinner) widget).getSelection());
            break;
        case STRING:
            value = ((Text) widget).getText();
            break;
        default:
            // shouldn't get here
            value = "";
            break;
        }
        return value;
    }
}
