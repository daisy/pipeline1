package org.daisy.util.jface;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractDetailsDialog extends IconAndMessageDialog {

	/**
	 * The Details button.
	 */
	private Button detailsButton;
	private Control detailsArea;
	private boolean detailsCreated = false;
	private Point detailsSize = null;
	private String title;

	public AbstractDetailsDialog(Shell parentShell, String title, String message) {
		super(parentShell);
		this.title = title;
		this.message = message;
	}

	/*
	 * (non-Javadoc) Method declared on Dialog. Handles the pressing of the Ok
	 * or Details button in this dialog. If the Ok button was pressed then close
	 * this dialog. If the Details button was pressed then toggle the displaying
	 * of the error details area. Note that the Details button will only be
	 * visible if the error being displayed specifies child details.
	 */
	@Override
	protected void buttonPressed(int id) {
		if (id == IDialogConstants.DETAILS_ID) {
			// was the details button pressed?
			toggleDetailsArea();
		} else {
			super.buttonPressed(id);
		}
	}

	/*
	 * (non-Javadoc) Method declared in Window.
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Details buttons
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createDetailsButton(parent);
	}

	/**
	 * Create the details button if it should be included.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	protected void createDetailsButton(Composite parent) {
		detailsButton = createButton(parent, IDialogConstants.DETAILS_ID,
				IDialogConstants.SHOW_DETAILS_LABEL, false);
	}

	/**
	 * This implementation of the <code>Dialog</code> framework method creates
	 * and lays out a composite. Subclasses that require a different dialog area
	 * may either override this method, or call the <code>super</code>
	 * implementation and add controls to the created composite.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		createMessageArea(parent);
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.numColumns = 2;
		composite.setLayout(layout);
		GridData childData = new GridData(GridData.FILL_BOTH);
		childData.horizontalSpan = 2;
		composite.setLayoutData(childData);
		composite.setFont(parent.getFont());

		return composite;
	}

	/**
	 * Create this dialog's details component.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the details control
	 */
	protected abstract Control createDetailsArea(Composite parent);

	protected void adjustDetailsLayout(GridData data) {
		// Do Nothing by default

	}

	private Control doCreateDetailsArea(Composite parent) {
		detailsArea = createDetailsArea(parent);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL);
		data.horizontalSpan = 2;
		adjustDetailsLayout(data);
		detailsArea.setLayoutData(data);
		detailsCreated = true;
		return detailsArea;
	}

	/**
	 * Show the details portion of the dialog if it is not already visible. This
	 * method will only work when it is invoked after the control of the dialog
	 * has been set. In other words, after the <code>createContents</code>
	 * method has been invoked and has returned the control for the content area
	 * of the dialog. Invoking the method before the content area has been set
	 * or after the dialog has been disposed will have no effect.
	 * 
	 */
	protected final void showDetailsArea() {
		if (!detailsCreated) {
			Control control = getContents();
			if ((control != null) && !control.isDisposed()) {
				toggleDetailsArea();
			}
		}
	}

	/**
	 * Toggles the unfolding of the details area. This is triggered by the user
	 * pressing the details button.
	 */
	private void toggleDetailsArea() {
		Point windowSize = getShell().getSize();
		if (detailsCreated) {
			detailsSize = detailsArea.getSize();
			detailsArea.dispose();
			detailsCreated = false;
			detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
			getShell().setSize(windowSize.x, windowSize.y - detailsSize.y);
		} else {
			detailsArea = doCreateDetailsArea((Composite) getDialogArea());
			detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
			if (detailsSize == null) {
				GridData gd = (GridData) detailsArea.getLayoutData();
				detailsSize = detailsArea.computeSize(gd.widthHint,
						gd.heightHint);
			} else {
				detailsArea.setSize(detailsSize);
			}
			getShell().setSize(windowSize.x, windowSize.y + detailsSize.y);
		}
	}
}
