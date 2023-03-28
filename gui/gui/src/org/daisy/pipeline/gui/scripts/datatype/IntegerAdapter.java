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
import org.daisy.pipeline.core.script.datatype.IntegerDatatype;
import org.daisy.pipeline.gui.util.CheckUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;

/**
 * Used to edit script parameters of type {@link IntegerDatatype}. Uses a
 * {@link Spinner} widget.
 * 
 * @author Romain Deltour
 * 
 */
public class IntegerAdapter extends DefaultAdapter {
	/**
	 * Create the adapter for <code>param</code> and adds the widgets to
	 * <code>parent</code>.
	 * 
	 * @param parent
	 *            The parent composite of the adapter widgets.
	 * @param param
	 *            The parameter to edit.
	 */
	public IntegerAdapter(Composite parent, ScriptParameter param) {
		super(parent, (param.getDatatype() instanceof IntegerDatatype) ? param
				: CheckUtil.illegalArgument(param,
						"Invalid parameter type: the type of " //$NON-NLS-1$
								+ param.getName() + " is " //$NON-NLS-1$
								+ param.getDatatype()));
	}

	@Override
	public Control doCreateControl(Composite parent) {
		IntegerDatatype type = (IntegerDatatype) param.getDatatype();
		Spinner spin = new Spinner(parent, SWT.NONE);
		spin.setMinimum(type.getMin());
		spin.setMaximum(type.getMax());
		return spin;
	}

	@Override
	public String getValue() {
		return Integer.toString(((Spinner) control).getSelection());
	}

	@Override
	public void setValue(String value) {
		((Spinner) control).setSelection(Integer.parseInt(value));
	}

}
