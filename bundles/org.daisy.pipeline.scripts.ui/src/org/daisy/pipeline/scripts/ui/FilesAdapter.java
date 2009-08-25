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
package org.daisy.pipeline.scripts.ui;

import java.io.File;

import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.datatype.FilesDatatype;
import org.daisy.util.swt.DialogHelper;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * Used to edit script parameters of type {@link FilesDatatype}. Uses a
 * {@link Text} widget with a "Browse" button.
 * 
 * @author Romain Deltour
 * 
 */
public class FilesAdapter extends DefaultAdapter {
	/**
	 * Create the adapter for <code>param</code> and adds the widgets to
	 * <code>parent</code>.
	 * 
	 * @param parent
	 *            The parent composite of the adapter widgets.
	 * @param param
	 *            The parameter to edit.
	 */
	public FilesAdapter(Composite parent, ScriptParameter param) {
		super(parent, (param.getDatatype() instanceof FilesDatatype) ? param
				: illegalArgument(param, "Invalid parameter type: the type of " //$NON-NLS-1$
						+ param.getName() + " is " //$NON-NLS-1$
						+ param.getDatatype()));
	}

	@Override
	public Control doCreateControl(final Composite parent) {
		final FilesDatatype type = (FilesDatatype) param.getDatatype();
		final Text field = new Text(parent, SWT.SINGLE | SWT.BORDER);
		Button button = new Button(parent, SWT.PUSH | SWT.CENTER);
		button.setText(JFaceResources.getString("openBrowse")); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// File file = new File(PreferencesUtil.get(
				// type.isInput() ? PreferencesKeys.LAST_SELECTED_INPUT
				// : PreferencesKeys.LAST_SELECTED_OUTPUT, field
				// .getText()));
				File file = null;
				int style = (type.isInput()) ? SWT.OPEN : SWT.SAVE;
				String path = DialogHelper.browseFile(parent.getShell(), file,
						SWT.MULTI | style, type.getMime());
				if (path != null) {
					field.setText(path);
					// PreferencesUtil
					// .put(
					// type.isInput() ? PreferencesKeys.LAST_SELECTED_INPUT
					// : PreferencesKeys.LAST_SELECTED_OUTPUT,
					// path, new InstanceScope());
				}
			}
		});
		return field;
	}

	@Override
	public int getNumberOfControls() {
		return 3;
	}

	@Override
	public String getValue() {
		String value = super.getValue();
		return value.replace(File.pathSeparatorChar,
				FilesDatatype.SEPARATOR_CHAR);
	}

	@Override
	public void setValue(String value) {
		super.setValue(value.replace(FilesDatatype.SEPARATOR_CHAR,
				File.pathSeparatorChar));
	}

}
