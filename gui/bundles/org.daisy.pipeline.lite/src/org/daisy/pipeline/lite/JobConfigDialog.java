package org.daisy.pipeline.lite;

import java.util.ArrayList;
import java.util.List;

import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.core.script.ScriptParameter;
import org.daisy.pipeline.core.script.datatype.DatatypeException;
import org.daisy.pipeline.lite.internal.Images;
import org.daisy.pipeline.scripts.ui.DatatypeAdapter;
import org.daisy.pipeline.scripts.ui.DatatypeAdapterFactory;
import org.daisy.pipeline.scripts.ui.DatatypeAdapterValueListener;
import org.daisy.util.swt.ToggleArea;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class JobConfigDialog extends Dialog {

	private Script script;
	private Job job;
	private List<DatatypeAdapter> paramControls;
	private ToggleArea toggleArea;
	private boolean isInitializing;
	private boolean hasOptParams = false;
	private Button detailsButton;

	protected JobConfigDialog(Shell parentShell, Script script) {
		this(parentShell, new Job(script));
	}

	protected JobConfigDialog(Shell parentShell, Job job) {
		super(parentShell);
		this.script = job.getScript();
		this.job = job;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(job.getScript().getNicename());
		shell.setImage(Images.getImage(Images.PIPELINE_LOGO));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite control = (Composite) super.createDialogArea(parent);
		control.setLayout(new GridLayout(1, true));
		// Create controls for required and optional parameters
		ScriptParameter[] reqParams = script.getRequiredParameters().values()
				.toArray(new ScriptParameter[0]);
		ScriptParameter[] optParams = script.getOptionalParameters().values()
				.toArray(new ScriptParameter[0]);
		paramControls = new ArrayList<DatatypeAdapter>(reqParams.length
				+ optParams.length);
		if (reqParams.length > 0) {
			Composite reqGroup = new Composite(control, SWT.NONE);
			reqGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			paramControls.addAll(DatatypeAdapterFactory.createParamControls(
					reqGroup, reqParams));
		}
		toggleArea = null;
		if (optParams.length > 0) {
			// Note: createDialogArea is called before createButtonsArea
			// we can initialize the hasOptParams flag here
			hasOptParams = true;
			toggleArea = new ToggleArea(control, getShell());
			Composite optGroup = toggleArea.getControl();
			optGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			Composite separatorComp = new Composite(optGroup, SWT.NONE);
			new Label(separatorComp, SWT.SEPARATOR | SWT.HORIZONTAL);
			paramControls.addAll(DatatypeAdapterFactory.createParamControls(
					optGroup, optParams));
			// Adjust the layout of the separator
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) optGroup.getLayout()).numColumns;
			separatorComp.setLayoutData(gd);
			FillLayout sepLayout = new FillLayout();
			sepLayout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			sepLayout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			separatorComp.setLayout(sepLayout);
		}
		hookListeners();
		// TODO init param value from job
		return control;
	}

	@Override
	public void create() {
		super.create();
		initContent();
		updatePageComplete();
		getShell().pack();
		if (toggleArea != null) {
			toggleArea.show(false);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if (hasOptParams) {
			detailsButton = createButton(parent, IDialogConstants.DETAILS_ID,
					Messages.getString("common.action.advanced"), false); //$NON-NLS-1$
		}
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
		if (IDialogConstants.DETAILS_ID == buttonId) {
			toggleArea.toggle();
			detailsButton.setText(toggleArea.isShown() ? Messages.getString("common.action.hide") //$NON-NLS-1$
					: Messages.getString("common.action.advanced")); //$NON-NLS-1$
		}
	}

	@Override
	protected void setButtonLayoutData(Button button) {
		super.setButtonLayoutData(button);
		if (button.getData().equals(IDialogConstants.DETAILS_ID)) {
			GridData data = (GridData) button.getLayoutData();
			// Tweak the layout to add add a space after the "advanced" button
			((GridLayout) button.getParent().getLayout()).numColumns++;
			data.horizontalSpan = 2;
			data.horizontalAlignment = SWT.BEGINNING;
			button.setLayoutData(data);
		}
	}

	/**
	 * Hook focus and value change listeners to the parameter adapters.
	 */
	private void hookListeners() {
		for (final DatatypeAdapter adapter : paramControls) {
			// Update the job when parameters value changes
			adapter.addValueListener(new DatatypeAdapterValueListener() {
				public void valueChanged(DatatypeAdapter adapter) {
					if (isInitializing) {
						return;
					}
					String value = adapter.getValue();
					try {
						job.setParameterValue(adapter.getParameter().getName(),
								value);
					} catch (DatatypeException e) {
						// Nothing to do
					}
					updatePageComplete();
				}
			});
		}
	}

	private void initContent() {
		isInitializing = true;
		// Initialize from default values
		for (DatatypeAdapter adapter : paramControls) {
			ScriptParameter param = adapter.getParameter();
			String value = param.getValue();
			if ((value != null) && (value.length() > 0)) {
				adapter.setValue(param.getValue());
				try {
					job.setParameterValue(param.getName(), param.getValue());
				} catch (DatatypeException e) {
					// Nothing to do
				}
			}
		}
		isInitializing = false;
	}

	/**
	 * Checks that all the required parameters are set before enabling the OK
	 * button.
	 */
	private void updatePageComplete() {
		getButton(IDialogConstants.OK_ID).setEnabled(
				job.allRequiredParametersSet());
	}

}
