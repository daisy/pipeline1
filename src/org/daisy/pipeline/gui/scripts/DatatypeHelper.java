package org.daisy.pipeline.gui.scripts;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.BooleanDatatype;
import org.daisy.dmfc.core.script.datatype.Datatype;
import org.daisy.dmfc.core.script.datatype.DirectoryDatatype;
import org.daisy.dmfc.core.script.datatype.EnumDatatype;
import org.daisy.dmfc.core.script.datatype.EnumItem;
import org.daisy.dmfc.core.script.datatype.FileDatatype;
import org.daisy.dmfc.core.script.datatype.FilesDatatype;
import org.daisy.dmfc.core.script.datatype.IntegerDatatype;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.util.DialogHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
        case FILES:
            return 2;
        case INTEGER:
            return 1;
        case STRING:
            return 1;
        default:
            return 1;
        }
    }

    public static Control createControl(final Composite parent,
            ScriptParameter param, int numCol) {
        Datatype type = param.getDatatype();
        if (type == null) {
            GuiPlugin.get().error(
                    "Null datatype for parameter" + param.getName(), null);
            return emptyControl(parent, param, numCol);
        }
        switch (type.getType()) {
        case BOOLEAN:
            BooleanDatatype bType = (BooleanDatatype) type;
            Combo bCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
            bCombo.add(bType.getTrueValue());
            bCombo.add(bType.getFalseValue());
            bCombo.setData(param);
            final GridData bData = new GridData();
            bData.horizontalSpan = numCol;
            bData.horizontalAlignment = GridData.FILL;
            bData.grabExcessHorizontalSpace = true;
            bCombo.setLayoutData(bData);
            return bCombo;
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
            dField.setData(param);
            final GridData dfData = new GridData();
            dfData.horizontalSpan = numCol - 1;
            dfData.horizontalAlignment = GridData.FILL;
            dfData.grabExcessHorizontalSpace = true;
            dField.setLayoutData(dfData);
            return dField;
        case ENUM:
            EnumDatatype eType = (EnumDatatype) type;
            Combo eCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
            for (EnumItem item : eType.getItems()) {
                eCombo.add(item.getNiceName());
            }
            eCombo.setData(param);
            final GridData eData = new GridData();
            eData.horizontalSpan = numCol;
            eData.horizontalAlignment = GridData.FILL;
            eData.grabExcessHorizontalSpace = true;
            eCombo.setLayoutData(eData);
            return eCombo;
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
            fField.setData(param);
            final GridData fData = new GridData();
            fData.horizontalSpan = numCol - 1;
            fData.horizontalAlignment = GridData.FILL;
            fData.grabExcessHorizontalSpace = true;
            fField.setLayoutData(fData);
            return fField;
        case FILES:
            final FilesDatatype ffType = (FilesDatatype) type;
            final Text ffField = new Text(parent, SWT.SINGLE | SWT.BORDER);
            Button ffButton = new Button(parent, SWT.PUSH | SWT.CENTER);
            ffButton.setText("Browse...");
            ffButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    File file = new File(ffField.getText());
                    int style = (ffType.isInput()) ? SWT.OPEN : SWT.SAVE;
                    String path = DialogHelper.browseFile(parent.getShell(),
                            file, SWT.MULTI | style, ffType.getMime());
                    if (path != null) {
                        ffField.setText(path);
                    }
                }
            });
            ffField.setData(param);
            final GridData ffData = new GridData();
            ffData.horizontalSpan = numCol - 1;
            ffData.horizontalAlignment = GridData.FILL;
            ffData.grabExcessHorizontalSpace = true;
            ffField.setLayoutData(ffData);
            return ffField;
        case INTEGER:
            IntegerDatatype iType = (IntegerDatatype) type;
            Spinner iSpin = new Spinner(parent, SWT.NONE);
            iSpin.setMinimum(iType.getMin());
            iSpin.setMaximum(iType.getMax());
            iSpin.setData(param);
            final GridData iData = new GridData();
            iData.horizontalSpan = numCol;
            iData.horizontalAlignment = GridData.FILL;
            iData.grabExcessHorizontalSpace = true;
            iSpin.setLayoutData(iData);
            return iSpin;
        case STRING:
            Text sField = new Text(parent, SWT.SINGLE | SWT.BORDER);
            sField.setData(param);
            final GridData sData = new GridData();
            sData.horizontalSpan = numCol;
            sData.horizontalAlignment = GridData.FILL;
            sData.grabExcessHorizontalSpace = true;
            sField.setLayoutData(sData);
            return sField;

        default:
            GuiPlugin.get().error(
                    "Unknown datatype for parameter" + param.getName(), null);
            return emptyControl(parent, param, numCol);
        }
    }

    private static Control emptyControl(Composite parent,
            ScriptParameter param, int numCol) {
        Composite empty = new Composite(parent, SWT.NONE);
        empty.setData(param);
        final GridData data = new GridData();
        data.horizontalSpan = numCol;
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        empty.setLayoutData(data);
        return empty;
    }

    public static String getValue(Widget widget) {
        ScriptParameter param = (ScriptParameter) widget.getData();
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
        case FILES:
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

    public static void setValue(Widget widget, String value) {
        ScriptParameter param = (ScriptParameter) widget.getData();
        Datatype type = param.getDatatype();
        switch (type.getType()) {
        case BOOLEAN:
            ((Combo) widget).setText(value);
            break;
        case DIRECTORY:
            ((Text) widget).setText(value);
            break;
        case ENUM:
            ((Combo) widget).setText(value);
            break;
        case FILE:
            ((Text) widget).setText(value);
            break;
        case INTEGER:
            ((Spinner) widget).setSelection(Integer.parseInt(value));
            break;
        case STRING:
            ((Text) widget).setText(value);
            break;
        default:
            // shouldn't get here
        }
    }
}
