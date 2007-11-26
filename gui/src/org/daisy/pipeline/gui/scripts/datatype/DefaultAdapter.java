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

import org.daisy.pipeline.core.script.ScriptParameter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * The default concrete subclass of the <code>DatatypeAdapter</code>, which
 * uses a {@link Text} widget to edit the parameter value.
 * 
 * @author Romain Deltour
 * 
 */
public class DefaultAdapter extends DatatypeAdapter {

	/**
	 * Create the adapter for <code>param</code> and adds the widgets to
	 * <code>parent</code>.
	 * 
	 * @param parent
	 *            The parent composite of the adapter widgets.
	 * @param param
	 *            The parameter to edit.
	 */
	public DefaultAdapter(Composite parent, ScriptParameter param) {
		super(parent, param);
	}

	@Override
	public void adjustLayout(int numCol) {
		((GridData) control.getLayoutData()).horizontalSpan = numCol
				- getNumberOfControls() + 1;
	}

	/**
	 * Create the internal widgets and returns the main control.
	 * <p>
	 * This method calls {@link #doCreateLabel(Composite)} to create a label
	 * presenting the parameter name, then calls
	 * {@link #doCreateControl(Composite) to create the main control, and then
	 * sets layout data to these controls.}
	 * </p>
	 * 
	 * @param parent
	 *            The parent composite of this adapter's widgets.
	 * @return The main control used to edit the parameter.
	 */
	@Override
	protected Control createControl(Composite parent) {
		doCreateLabel(parent);
		Control control = doCreateControl(parent);
		control.setToolTipText(param.getDescription());
		final GridData data = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		control.setLayoutData(data);
		return control;
	}

	/**
	 * Creates the main control used to edit the underlying script parameter.
	 * 
	 * @param parent
	 *            The parent composite of this adapter's widget.
	 * @return The main control used to edit the parameter.
	 */
	protected Control doCreateControl(Composite parent) {
		return new Text(parent, SWT.SINGLE | SWT.BORDER);
	}

	/**
	 * Create the label widget that present the parameter name.
	 * 
	 * @param parent
	 *            The parent composite of this adapter's widget.
	 */
	protected void doCreateLabel(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(param.getNicename());
		label.setToolTipText(param.getDescription());
		GridData data = new GridData();
		data.horizontalAlignment = GridData.END;
		data.verticalAlignment = GridData.CENTER;
		data.grabExcessVerticalSpace = true;
		label.setLayoutData(data);
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	@Override
	public String getValue() {
		return ((Text) control).getText();
	}

	@Override
	protected void hookValueListener() {
		control.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event event) {
				System.out.println("hello");
				fireValueChanged();
			}
		});
	}

	@Override
	public void setValue(String value) {
		((Text) control).setText(value);
	}

}
