package org.daisy.pipeline.gui.scripts.datatype;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.BooleanDatatype;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Romain Deltour
 * 
 */
public class BooleanAdapter extends DefaultAdapter {

    @Override
    public Control createControl(Composite parent, ScriptParameter param,
            int numCol) {
        Composite empty = new Composite(parent,SWT.NONE);
        final GridData data2 = new GridData();
        data2.grabExcessHorizontalSpace=false;
        data2.grabExcessVerticalSpace=false;
        data2.horizontalAlignment=GridData.CENTER;
        data2.verticalAlignment=GridData.CENTER;
        data2.widthHint=0;
        data2.heightHint=0;
        empty.setLayoutData(data2);
        BooleanDatatype type = (BooleanDatatype) param.getDatatype();
        Button checkbox = new Button(parent, SWT.CHECK);
        checkbox.setText(param.getNicename());
        checkbox.setToolTipText(param.getDescription());
        checkbox.setData(param);
        checkbox.setSelection(type.getTrueValue().equals(param.getValue()));
        final GridData data = new GridData();
        data.horizontalSpan = numCol-1;
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        checkbox.setLayoutData(data);
        return checkbox;
        // createLabel(parent, param);
        // BooleanDatatype type = (BooleanDatatype) param.getDatatype();
        // Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        // combo.add(type.getTrueValue());
        // combo.add(type.getFalseValue());
        // combo.setData(param);
        // final GridData data = new GridData();
        // data.horizontalSpan = numCol-1;
        // data.horizontalAlignment = GridData.FILL;
        // data.grabExcessHorizontalSpace = true;
        // combo.setLayoutData(data);
        // return combo;
    }

    @Override
    public String getValue(Widget widget) {
        boolean checked = ((Button) widget).getSelection();
        BooleanDatatype type = (BooleanDatatype) ((ScriptParameter) widget
                .getData()).getDatatype();
        return (checked) ? type.getTrueValue() : type.getFalseValue();
    }

    @Override
    public void setValue(Widget widget, String value) {
        BooleanDatatype type = (BooleanDatatype) ((ScriptParameter) widget
                .getData()).getDatatype();
        ((Button) widget).setEnabled(type.getTrueValue().equals(value));
    }

}
