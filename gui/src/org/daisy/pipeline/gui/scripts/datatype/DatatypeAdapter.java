/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.gui.scripts.datatype;

import java.util.HashMap;
import java.util.Map;

import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.datatype.Datatype;
import org.daisy.pipeline.gui.GuiPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Romain Deltour
 * 
 */
public abstract class DatatypeAdapter {
    public static final String LAST_SELECTED_INPUT = "LAST_SELECTED_INPUT"; //$NON-NLS-1$
    public static final String LAST_SELECTED_OUTPUT = "LAST_SELECTED_OUTPUT"; //$NON-NLS-1$

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
            sb.append("Adapter"); //$NON-NLS-1$
            try {
                Class clazz = Class.forName(sb.toString());
                adapter = (DatatypeAdapter) clazz.newInstance();
            } catch (Exception e) {
                GuiPlugin.get().error(
                        "Cannot instantiate adapter for datatype " + type, e); //$NON-NLS-1$
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
