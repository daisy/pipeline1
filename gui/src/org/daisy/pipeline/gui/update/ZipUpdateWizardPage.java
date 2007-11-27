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
package org.daisy.pipeline.gui.update;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.PreferencesKeys;
import org.daisy.pipeline.gui.PreferencesUtil;
import org.daisy.pipeline.gui.util.DialogHelper;
import org.daisy.pipeline.gui.util.ZipStructure;
import org.daisy.pipeline.gui.util.viewers.ZipContentProvider;
import org.daisy.pipeline.gui.util.viewers.ZipLabelProvider;
import org.daisy.util.mime.MIMEConstants;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * The wizard page used to apply a ZIP patch for software update.
 * 
 * @author Romain Deltour
 * 
 */
public class ZipUpdateWizardPage extends WizardPage {

	/** The name used to identify this wizard page */
	private static final String NAME = "zipUpdate";
	/** The field for the path to the update patch */
	protected Text zipPathField;
	/** The "browse..." button to invoke a file selector dialog */
	protected Button zipBrowseButton;
	/** The area used to display the update patch description */
	protected Text zipDescriptionText;
	/** The tree viewer used to display the content of the update patch */
	protected TreeViewer zipViewer;
	/** The content of the ZIP file selected by the user */
	protected ZipStructure zipStructure;
	/** The filter applied to the ZIP content (for ignoring metadata files) */
	protected ZipUpdateFilter zipFilter = new ZipUpdateFilter();

	private boolean isFieldDirty;

	/**
	 * Creates a new page with default name and title.
	 */
	protected ZipUpdateWizardPage() {
		super(NAME);
		setTitle("Apply a ZIP Update Patch");
		setDescription("Select a ZIP archive containing the update patch to install.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		// Top Level control
		Composite control = new Composite(parent, SWT.NULL);
		control.setLayout(new GridLayout(3, false));
		// Zip Path Field
		Label zipPathLabel = new Label(control, SWT.NONE);
		zipPathLabel.setText("Zip Update Patch:");
		zipPathField = new Text(control, SWT.SINGLE | SWT.BORDER);
		zipPathField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		zipBrowseButton = new Button(control, SWT.PUSH | SWT.CENTER);
		zipBrowseButton.setText("Browse...");
		// SashForm
		SashForm sash = new SashForm(control, SWT.VERTICAL);
		GridData sashGD = new GridData(GridData.FILL_BOTH);
		sashGD.horizontalSpan = 3;
		sashGD.heightHint = 300;
		sash.setLayoutData(sashGD);
		Composite descrArea = new Composite(sash, SWT.NONE);
		descrArea.setLayout(new GridLayout());
		Composite treeArea = new Composite(sash, SWT.NONE);
		treeArea.setLayout(new GridLayout());
		sash.setWeights(new int[] { 1, 2 });
		// Description Area
		Label descriptionLabel = new Label(descrArea, SWT.NONE);
		descriptionLabel.setText("Description:");
		zipDescriptionText = new Text(descrArea, SWT.READ_ONLY | SWT.MULTI
				| SWT.V_SCROLL | SWT.BORDER);
		zipDescriptionText.setLayoutData(new GridData(GridData.FILL_BOTH));
		// Zip Tree Viewer
		Label treeLabel = new Label(treeArea, SWT.NONE);
		treeLabel.setText("Content:");
		zipViewer = new TreeViewer(treeArea);
		zipViewer.setContentProvider(new ZipContentProvider());
		zipViewer.setLabelProvider(new ZipLabelProvider());
		zipViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		// Finalize
		hookListeners();
		updatePageComplete();
		setControl(control);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (zipStructure != null) {
			try {
				zipStructure.getZipFile().close();
			} catch (IOException e) {
				GuiPlugin.get().error("Couldn't close the ZIP update patch", e);
			}
		}

	}

	/**
	 * Execute the update operation from the selected zip file.
	 * <p>
	 * This metod is called by the wizard when the user selects the "Finish"
	 * button (see {@link IWizard#performFinish()}).
	 * </p>
	 * 
	 * @see ZipUpdateOperation
	 * 
	 * @return <code>true</code> if and only if the update operation
	 *         succeeded.
	 */
	public boolean finish() {
		ZipUpdateOperation operation = new ZipUpdateOperation(zipStructure,
				getShell());

		try {
			getContainer().run(true, false, operation);
		} catch (InterruptedException e) {
			GuiPlugin.get().info(e.getMessage(), e);
			operation.revert();
			return false;
		} catch (InvocationTargetException e) {
			MessageDialog
					.openError(getContainer().getShell(), "Update Error",
							"Could not apply the update patch. See the error log for details.");
			GuiPlugin.get().error(e.getMessage(), e);
			operation.revert();
			return false;
		}
		IStatus status = operation.getStatus();
		if (!status.isOK()) {
			ErrorDialog.openError(getContainer().getShell(), "Update Error",
					null, status);
			operation.revert();
			return false;
		} else {
			boolean restart = MessageDialog
					.openQuestion(
							getContainer().getShell(),
							"Update Succeeded",
							"The application wil be updated on the next restart.\nDo you want to restart now ?");
			return (restart) ? PlatformUI.getWorkbench().restart() : true;
		}

	}

	/**
	 * Adds event listeners to the widgets created in
	 * {@link #createControl(Composite)}.
	 */
	protected void hookListeners() {
		// Zip Field
		zipPathField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// If there has been a key pressed then mark as dirty
				isFieldDirty = true;
			}
		});
		zipPathField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				// Clear the flag to prevent constant update
				if (isFieldDirty) {
					isFieldDirty = false;
					updatePageComplete();
				}
			}
		});

		// Browse Button
		zipBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File file = new File(PreferencesUtil.get(
						PreferencesKeys.UPDATE_LAST_SELECTED_ZIP, zipPathField
								.getText()));
				String path = DialogHelper.browseFile(getShell(), file,
						SWT.SINGLE | SWT.OPEN,
						MIMEConstants.MIME_APPLICATION_ZIP);
				if (path != null) {
					zipPathField.setText(path);
					updatePageComplete();
					PreferencesUtil.put(
							PreferencesKeys.UPDATE_LAST_SELECTED_ZIP, path,
							new ConfigurationScope());
				}
			}
		});
	}

	/**
	 * Reload the zip structure from the user selected zip path.
	 */
	private void reloadZipStructure() {
		zipStructure = null;
		// Get the zip file
		ZipFile zipFile = null;
		String path = zipPathField.getText();
		if (path.length() == 0) {
			return;
		}
		try {
			zipFile = new ZipFile(path);
		} catch (ZipException e) {
			setErrorMessage("Invalid Zip file.");
		} catch (IOException e) {
			setErrorMessage("Zip file could not be read.");
		}
		// Reset the description area
		ZipUpdateMetadata metadata = new ZipUpdateMetadata(zipFile);
		zipDescriptionText.setText(metadata.getDescription());
		// Finally reset the zip structure
		zipStructure = new ZipStructure(zipFile, zipFilter);
	}

	/**
	 * Check whether this wizard page is complete, i.e. the user has selected an
	 * existing archive.
	 * 
	 */
	protected void updatePageComplete() {
		setPageComplete(false);
		setErrorMessage(null);
		reloadZipStructure();
		setPageComplete(zipStructure != null);
		zipViewer.setInput(zipStructure);
		zipViewer.expandAll();
		zipPathField.setFocus();
	}
}
