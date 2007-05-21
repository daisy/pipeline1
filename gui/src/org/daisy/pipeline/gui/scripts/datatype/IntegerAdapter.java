package org.daisy.pipeline.gui.scripts.datatype;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.IntegerDatatype;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Romain Deltour
 * 
 */
public class IntegerAdapter extends DefaultAdapter {

    @Override
    public Control createControl(Composite parent, ScriptParameter param,
            int numCol) {
        createLabel(parent, param);
        IntegerDatatype type = (IntegerDatatype) param.getDatatype();
        Spinner spin = new Spinner(parent, SWT.NONE);
        spin.setMinimum(type.getMin());
        spin.setMaximum(type.getMax());
        spin.setData(param);
        final GridData data = new GridData();
        data.horizontalSpan = numCol-1;
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        spin.setLayoutData(data);
        return spin;
    }

    @Override
    public String getValue(Widget widget) {
        return Integer.toString(((Spinner) widget).getSelection());
    }

    @Override
    public void setValue(Widget widget, String value) {
        ((Spinner) widget).setSelection(Integer.parseInt(value));
    }

}
