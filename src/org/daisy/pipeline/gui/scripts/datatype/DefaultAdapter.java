package org.daisy.pipeline.gui.scripts.datatype;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Romain Deltour
 * 
 */
public class DefaultAdapter extends DatatypeAdapter {

    @Override
    public Control createControl(Composite parent, ScriptParameter param,
            int numCol) {
        Text field = new Text(parent, SWT.SINGLE | SWT.BORDER);
        field.setData(param);
        final GridData data = new GridData();
        data.horizontalSpan = numCol;
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        field.setLayoutData(data);
        return field;
    }

    @Override
    public int getNumCol() {
        return 1;
    }

    @Override
    public String getValue(Widget widget) {
        return ((Text) widget).getText();
    }

    @Override
    public void setValue(Widget widget, String value) {
        ((Text) widget).setText(value);
    }

}
