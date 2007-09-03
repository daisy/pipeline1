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

import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.datatype.BooleanDatatype;
import org.daisy.pipeline.gui.util.CheckUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Used to edit script parameters of type {@link BooleanDatatype}. Uses a
 * {@link Button} widget of type {@link SWT#CHECK}.
 * 
 * @author Romain Deltour
 * 
 */
public class BooleanAdapter extends DefaultAdapter {

	/**
	 * Create the adapter for <code>param</code> and adds the widgets to
	 * <code>parent</code>.
	 * 
	 * @param parent
	 *            The parent composite of the adapter widgets.
	 * @param param
	 *            The parameter to edit.
	 */
	public BooleanAdapter(Composite parent, ScriptParameter param) {
		super(parent, (param.getDatatype() instanceof BooleanDatatype) ? param
				: CheckUtil.illegalArgument(param,
						"Invalid parameter type: the type of "
								+ param.getName() + " is "
								+ param.getDatatype()));
	}

	@Override
	protected Control doCreateControl(Composite parent) {
		BooleanDatatype type = (BooleanDatatype) param.getDatatype();
		Button checkbox = new Button(parent, SWT.CHECK);
		checkbox.setText(param.getNicename());
		checkbox.setToolTipText(param.getDescription());
		checkbox.setData(param);
		checkbox.setSelection(type.getTrueValue().equals(param.getValue()));
		return checkbox;
	}

	@Override
	protected void doCreateLabel(Composite parent) {
		// Create an empty composite, the label is part of the checkbox button
		Composite empty = new Composite(parent, SWT.NONE);
		final GridData data2 = new GridData();
		data2.grabExcessHorizontalSpace = false;
		data2.grabExcessVerticalSpace = false;
		data2.horizontalAlignment = GridData.CENTER;
		data2.verticalAlignment = GridData.CENTER;
		data2.widthHint = 0;
		data2.heightHint = 0;
		empty.setLayoutData(data2);
	}

	@Override
	public String getValue() {
		boolean checked = ((Button) control).getSelection();
		BooleanDatatype type = (BooleanDatatype) param.getDatatype();
		return (checked) ? type.getTrueValue() : type.getFalseValue();
	}

	@Override
	protected void hookValueListener() {
		control.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				fireValueChanged();
			}
		});
	}

	@Override
	public void setValue(String value) {
		BooleanDatatype type = (BooleanDatatype) param.getDatatype();
		((Button) control).setSelection(type.getTrueValue().equals(value));
	}

}
