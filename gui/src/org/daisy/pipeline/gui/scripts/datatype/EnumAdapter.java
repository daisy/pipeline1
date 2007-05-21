package org.daisy.pipeline.gui.scripts.datatype;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.EnumDatatype;
import org.daisy.dmfc.core.script.datatype.EnumItem;
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
public class EnumAdapter extends DefaultAdapter {

    @Override
    public Control createControl(Composite parent, ScriptParameter param,
            int numCol) {
        createLabel(parent, param);
        EnumDatatype type = (EnumDatatype) param.getDatatype();
        Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        for (EnumItem item : type.getItems()) {
            combo.add(item.getNiceName());
        }
        combo.setData(param);
        final GridData data = new GridData();
        data.horizontalSpan = numCol-1;
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        combo.setLayoutData(data);
        return combo;
    }

    @Override
    public String getValue(Widget widget) {
        String name = ((Combo) widget).getText();
        EnumDatatype type = (EnumDatatype) ((ScriptParameter) widget.getData())
                .getDatatype();
        return getValueFromNiceName(type, name);
    }

    @Override
    public void setValue(Widget widget, String value) {
        EnumDatatype type = (EnumDatatype) ((ScriptParameter) widget.getData())
                .getDatatype();
        ((Combo) widget).setText(getNiceNameFromValue(type, value));
    }

    private String getValueFromNiceName(EnumDatatype type, String name) {
        for (EnumItem item : type.getItems()) {
            if (name.equals(item.getNiceName())) {
                return item.getValue();
            }
        }
        return null;
    }

    private String getNiceNameFromValue(EnumDatatype type, String value) {
        for (EnumItem item : type.getItems()) {
            if (value.equals(item.getValue())) {
                return item.getNiceName();
            }
        }
        return null;
    }
}
