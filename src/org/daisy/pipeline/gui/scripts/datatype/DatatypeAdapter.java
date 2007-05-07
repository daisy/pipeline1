package org.daisy.pipeline.gui.scripts.datatype;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.Datatype;
import org.daisy.pipeline.gui.GuiPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Romain Deltour
 * 
 */
public abstract class DatatypeAdapter {
    private static Map<Datatype.Type, DatatypeAdapter> adapterMap = new HashMap<Datatype.Type, DatatypeAdapter>();

    public static DatatypeAdapter getAdapter(Datatype type) {
        DatatypeAdapter adapter = adapterMap.get(type.getType());
        if (adapter == null) {
            StringBuilder sb = new StringBuilder();
            sb.append(DatatypeAdapter.class.getPackage().getName());
            sb.append('.');
            String typeName = type.getType().name();
            sb.append(typeName.charAt(0));
            sb.append(typeName.substring(1).toLowerCase());
            sb.append("Adapter");
            try {
                Class clazz = Class.forName(sb.toString());
                adapter = (DatatypeAdapter) clazz.newInstance();
            } catch (Exception e) {
                GuiPlugin.get().error(
                        "Cannot instantiate adapter for datatype " + type, e);
            }
            if (adapter == null) {
                adapter = new DefaultAdapter();
            }
            adapterMap.put(type.getType(), adapter);
        }
        return adapter;
    }

    public abstract Control createControl(Composite parent,
            ScriptParameter param, int numCol);

    public abstract int getNumCol();

    public abstract String getValue(Widget widget);

    public abstract void setValue(Widget widget, String value);
}
