/*
 * DAISY Pipeline GUI Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.scripts.datatype;

import java.util.HashMap;
import java.util.Map;

import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.datatype.Datatype;
import org.daisy.pipeline.gui.GuiPlugin;
import org.eclipse.swt.widgets.Composite;

/**
 * A {@link Datatype}-aware factory for {@link DatatypeAdapter}s.
 * 
 * @author Romain Deltour
 * 
 */
public class DatatypeAdapterFactory {
	private static Map<Datatype.Type, Class<?>> classMap = new HashMap<Datatype.Type, Class<?>>();

	/**
	 * Creates a <code>DatatypeAdapter</code> instance for the given parameter
	 * and creates its controls as children of the given composite.
	 * <p>
	 * The {@link Datatype} of the given script parameter is checked and the
	 * corresponding adapter is returned.
	 * </p>
	 * 
	 * @param parent
	 *            The composite to which will be attached the internal controls
	 *            of the returned adapter.
	 * @param param
	 *            The script parameter to create an adapter for.
	 * @return A <code>DatatypeAdapter</code> which best suits the
	 *         <code>Datatype</code> of <code>param</code>.
	 */
	public static DatatypeAdapter createAdapter(Composite parent,
			ScriptParameter param) {
		// Fetch the adapter class
		Datatype type = param.getDatatype();
		Class<?> clazz = classMap.get(type);
		if (clazz == null) {

			StringBuilder sb = new StringBuilder();
			sb.append(DatatypeAdapter.class.getPackage().getName());
			sb.append('.');
			String typeName = type.getType().name();
			sb.append(typeName.charAt(0));
			sb.append(typeName.substring(1).toLowerCase());
			sb.append("Adapter"); //$NON-NLS-1$
			try {
				clazz = Class.forName(sb.toString());
			} catch (ClassNotFoundException e) {
				GuiPlugin.get().error(
						"Cannot find adapter class for datatype " + type, e); //$NON-NLS-1$
			}
		}
		// Instantiate a new adapter
		DatatypeAdapter adapter = null;
		try {
			adapter = (DatatypeAdapter) clazz.getConstructor(Composite.class,
					ScriptParameter.class).newInstance(parent, param);
		} catch (Exception e) {
			GuiPlugin.get().error(
					"Cannot instantiate adapter for datatype " + type, e); //$NON-NLS-1$
		}
		return adapter;
	}

}
