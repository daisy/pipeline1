package org.daisy.pipeline.gui.scripts.datatype;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
        createLabel(parent,param);
        Text field = new Text(parent, SWT.SINGLE | SWT.BORDER);
        field.setData(param);
        final GridData data = new GridData();
        data.horizontalSpan = numCol-1;
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        field.setLayoutData(data);
        return field;
    }

    @Override
    public int getNumCol() {
        return 2;
    }

    @Override
    public String getValue(Widget widget) {
        return ((Text) widget).getText();
    }

    @Override
    public void setValue(Widget widget, String value) {
        ((Text) widget).setText(value);
    }
    
    protected void createLabel(Composite parent, ScriptParameter param) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(param.getNicename());
        label.setToolTipText(param.getDescription());
        GridData data = new GridData();
        data.horizontalAlignment = GridData.END;
        data.verticalAlignment = GridData.CENTER;
        data.grabExcessVerticalSpace = true;
        label.setLayoutData(data);
    }

}
