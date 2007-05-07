package org.daisy.pipeline.gui.scripts.datatype;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.BooleanDatatype;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
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
        BooleanDatatype type = (BooleanDatatype) param.getDatatype();
        Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        combo.add(type.getTrueValue());
        combo.add(type.getFalseValue());
        combo.setData(param);
        final GridData data = new GridData();
        data.horizontalSpan = numCol;
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        combo.setLayoutData(data);
        return combo;
    }

    @Override
    public String getValue(Widget widget) {
        return ((Combo) widget).getText();
    }

    @Override
    public void setValue(Widget widget, String value) {
        ((Combo) widget).setText(value);
    }

}
