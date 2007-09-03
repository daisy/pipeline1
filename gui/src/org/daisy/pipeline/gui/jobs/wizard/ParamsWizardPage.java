/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
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
package org.daisy.pipeline.gui.jobs.wizard;

import java.util.ArrayList;
import java.util.List;

import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.datatype.DatatypeException;
import org.daisy.pipeline.gui.scripts.datatype.DatatypeAdapter;
import org.daisy.pipeline.gui.scripts.datatype.DatatypeAdapterFactory;
import org.daisy.pipeline.gui.scripts.datatype.DatatypeAdapterValueListener;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

/**
 * Wizard page used to configure the Job parameters.
 * 
 * @author Romain Deltour
 * @see ScriptParameter
 * 
 */
public class ParamsWizardPage extends WizardPage implements
		DatatypeAdapterValueListener {

	/**
	 * An intermediate Composite used as the page content which can be resized
	 * dynamically to fit the number of parameters.
	 */
	public class PageControl extends Composite {

		/**
		 * Simply calls the parent Composite constructor.
		 * 
		 * @param parent
		 *            a widget which will be the parent of the new instance
		 *            (cannot be null)
		 * @param style
		 *            the style of widget to construct
		 */
		public PageControl(Composite parent, int style) {
			super(parent, style);
		}

		/**
		 * Returns the preferred size of this page control.
		 * <p>
		 * The size is computed so that all the parameters can be displayed by
		 * the wizard, but so that the width does not change.
		 * </p>
		 * 
		 * @return The preferred size of this page control, so that all the
		 *         parameters can be displayed by the wizard.
		 */
		@Override
		public Point computeSize(int wHint, int hHint, boolean changed) {
			Point size = super.computeSize(wHint, hHint, changed);
			Point firstSize = getWizard().getStartingPage().getControl()
					.getSize();
			size.x = firstSize.x;
			return size;
		}

	}

	/** The name used to identify this wizard page */
	public static final String NAME = "parameters"; //$NON-NLS-1$
	/**
	 * The list of <code>DatatypeAdapters</code> used to edit the script
	 * parameters
	 */
	private List<DatatypeAdapter> paramControls;
	/** Whether we're still in the initialization process. */
	private boolean isInitializing;

	/**
	 * Creates this wizard page. The title and first banner message are set
	 * later and dynamically in the {@link #createControl(Composite)} method.
	 */
	protected ParamsWizardPage() {
		super(NAME);
		// Note: title & message are set in #createControl
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite control = new PageControl(parent, SWT.NULL);
		control.setLayout(new GridLayout(1, true));
		setControl(control);
		Script script = ((NewJobWizard) getWizard()).getJob().getScript();
		// Set page title and message
		setTitle(NLS.bind(Messages.page_param_title, script.getNicename()));
		setDescription(NLS.bind(Messages.page_param_description, script
				.getNicename()));
		// Create controls for required and optional parameters
		ScriptParameter[] reqParams = script.getRequiredParameters().values()
				.toArray(new ScriptParameter[0]);
		ScriptParameter[] optParams = script.getOptionalParameters().values()
				.toArray(new ScriptParameter[0]);
		paramControls = new ArrayList<DatatypeAdapter>(reqParams.length
				+ optParams.length);
		if (reqParams.length > 0) {
			Group reqGroup = new Group(control, SWT.SHADOW_NONE);
			reqGroup.setText(Messages.page_param_requiredGroup);
			reqGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			paramControls.addAll(createParamControls(reqGroup, reqParams));
		}
		if (optParams.length > 0) {
			Group optGroup = new Group(control, SWT.SHADOW_NONE);
			optGroup.setText(Messages.page_param_optionalGroup);
			optGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			paramControls.addAll(createParamControls(optGroup, optParams));
		}
		// Listen to controls modification
		hookListeners();
		// Initialize content
		initContent();
		updatePageComplete();
	}

	/**
	 * Creates the adapters for the given set of parameters and attaches them to
	 * the given parent composite.
	 * 
	 * @param parent
	 *            The composite that will be the parent of the created widgets
	 * @param params
	 *            The parameter to create adapters for
	 * @return The list of newly created adapters.
	 */
	private List<DatatypeAdapter> createParamControls(Composite parent,
			ScriptParameter[] params) {
		List<DatatypeAdapter> adapters = new ArrayList<DatatypeAdapter>(
				params.length);
		int numCol = 0;
		// Create the controls
		for (ScriptParameter param : params) {
			DatatypeAdapter adapter = DatatypeAdapterFactory.createAdapter(
					parent, param);
			adapters.add(adapter);
			numCol = Math.max(numCol, adapter.getNumberOfControls());
		}
		// Adjust the layout
		parent.setLayout(new GridLayout(numCol, false));
		for (DatatypeAdapter adapter : adapters) {
			adapter.adjustLayout(numCol);
		}
		return adapters;
	}

	/**
	 * Hook focus and value change listeners to the parameter adapters.
	 */
	private void hookListeners() {
		for (final DatatypeAdapter adapter : paramControls) {
			adapter.getControl().addListener(SWT.Selection | SWT.Modify,
					new Listener() {

						public void handleEvent(Event event) {

						}

					});
			// Update wizard message when parameters get/lose focus
			adapter.getControl().addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					setMessage(adapter.getParameter().getDescription(),
							INFORMATION);
				}

				public void focusLost(FocusEvent e) {
					setMessage(null);
				}
			});
			// Update the job when parameters value changes
			adapter.addValueListener(this);
		}
	}

	/**
	 * Initialize the page contents from the persisted dialog settings.
	 */
	private void initContent() {
		isInitializing = true;
		String scriptName = ((NewJobWizard) getWizard()).getJob().getScript()
				.getName();
		IDialogSettings scriptSettings = getDialogSettings().getSection(
				scriptName);
		if (scriptSettings == null) {
			scriptSettings = getDialogSettings().addNewSection(scriptName);
		}
		// Initialize from default values
		for (DatatypeAdapter adapter : paramControls) {
			ScriptParameter param = adapter.getParameter();
			String value;
			if (((NewJobWizard) getWizard()).isFirstInSession()) {
				// initialize settings with default values
				updateSettings(param, param.getValue());
			}
			value = scriptSettings.get(param.getName());
			if (value != null) {
				adapter.setValue(value);
				try {
					((NewJobWizard) getWizard()).getJob().setParameterValue(
							param.getName(), value);
				} catch (DatatypeException e) {
					// Nothing to do
				}
			}
		}
		isInitializing = false;
	}

	@Override
	public void performHelp() {
		((NewJobWizard) getWizard()).performHelp();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (!visible) {
			getControl().dispose();
			setControl(null);
		}
	}

	/**
	 * Checks that all the required parameters are set before enabling the
	 * finish button.
	 */
	void updatePageComplete() {
		setPageComplete(false);
		Job job = ((NewJobWizard) getWizard()).getJob();
		if (job.allRequiredParametersSet()) {
			setPageComplete(true);
		}
	}

	/**
	 * Updates the persisted dialog settings with the current parameter value.
	 * 
	 * @param param
	 *            The parameter for which to update the settings
	 * @param value
	 *            The new value to set for this parameter setting
	 */
	private void updateSettings(ScriptParameter param, String value) {
		String scriptName = ((NewJobWizard) getWizard()).getJob().getScript()
				.getName();
		IDialogSettings scriptSettings = getDialogSettings().getSection(
				scriptName);
		scriptSettings.put(param.getName(), value);
	}

	/**
	 * Listen to parameter adapter changes and update the created job and wizard
	 * status accordingly.
	 * 
	 * @param adapter
	 *            The parameter the value of which is listened to.
	 */
	public void valueChanged(DatatypeAdapter adapter) {
		if (isInitializing) {
			return;
		}
		Job job = ((NewJobWizard) getWizard()).getJob();
		String value = adapter.getValue();
		try {
			job.setParameterValue(adapter.getParameter().getName(), value);
			setErrorMessage(null);
			updateSettings(adapter.getParameter(), value);
		} catch (DatatypeException e) {
			setErrorMessage(NLS.bind(Messages.page_param_error_invalid, e
					.getLocalizedMessage()));
		}
		updatePageComplete();
	}
}
