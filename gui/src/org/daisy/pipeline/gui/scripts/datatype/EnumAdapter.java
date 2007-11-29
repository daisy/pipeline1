/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
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

import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.datatype.EnumDatatype;
import org.daisy.pipeline.core.script.datatype.EnumItem;
import org.daisy.pipeline.gui.util.CheckUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Used to edit script parameters of type {@link EnumDatatype}. Uses a
 * {@link Combo} widget.
 * 
 * @author Romain Deltour
 * 
 */
public class EnumAdapter extends DefaultAdapter {
	/**
	 * Create the adapter for <code>param</code> and adds the widgets to
	 * <code>parent</code>.
	 * 
	 * @param parent
	 *            The parent composite of the adapter widgets.
	 * @param param
	 *            The parameter to edit.
	 */
	public EnumAdapter(Composite parent, ScriptParameter param) {
		super(parent, (param.getDatatype() instanceof EnumDatatype) ? param
				: CheckUtil.illegalArgument(param,
						"Invalid parameter type: the type of " //$NON-NLS-1$
								+ param.getName() + " is " //$NON-NLS-1$
								+ param.getDatatype()));
	}

	@Override
	protected Control doCreateControl(Composite parent) {
		EnumDatatype type = (EnumDatatype) param.getDatatype();
		Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (EnumItem item : type.getItems()) {
			combo.add(item.getNiceName());
		}
		return combo;
	}

	private String getNiceNameFromValue(EnumDatatype type, String value) {
		for (EnumItem item : type.getItems()) {
			if (value.equals(item.getValue())) {
				return item.getNiceName();
			}
		}
		return null;
	}

	@Override
	public String getValue() {
		String name = ((Combo) control).getText();
		EnumDatatype type = (EnumDatatype) param.getDatatype();
		return getValueFromNiceName(type, name);
	}

	private String getValueFromNiceName(EnumDatatype type, String name) {
		for (EnumItem item : type.getItems()) {
			if (name.equals(item.getNiceName())) {
				return item.getValue();
			}
		}
		return null;
	}

	@Override
	public void setValue(String value) {
		EnumDatatype type = (EnumDatatype) param.getDatatype();
		((Combo) control).setText(getNiceNameFromValue(type, value));
	}
}
