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
package org.daisy.pipeline.gui.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * A message dialog asking for a selection in a list which also allows the user
 * to adjust a toggle setting. If a preference store is provided and the user
 * selects the toggle, then the user's answer (yes/ok or no) will be persisted
 * in the store. If no store is provided, then this information can be queried
 * after the dialog closes.
 * </p>
 * 
 * @author Romain Deltour
 * @see MessageDialogWithToggle
 * 
 */
public class ListSelectionMessageToggleDialog extends MessageDialogWithToggle {

	private static final String SELECT_ALL_LABEL = Messages.button_selectAll;

	private static final String DESELECT_ALL_LABEL = Messages.button_deselectAll;
	// sizing constants
	private final static int SIZING_SELECTION_WIDGET_HEIGHT = 150;

	private final static int SIZING_SELECTION_WIDGET_WIDTH = 100;
	// the final collection of selected elements, or null if this dialog was
	// canceled
	private Object[] result;

	// the root element to populate the viewer with
	private Object input;

	// a collection of the initially-selected elements
	private List<Object> initialSelections = new ArrayList<Object>();

	// providers for populating this dialog
	private ILabelProvider labelProvider;

	private IStructuredContentProvider contentProvider;

	// the visual selection widget group
	CheckboxTableViewer listViewer;

	/**
	 * Creates a dialog with toggle for selection in a list
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param dialogTitle
	 *            the dialog title or <code>null</code> if nonde
	 * @param image
	 *            the dialog title image, or <code>null</code> if none
	 * @param message
	 *            the dialog message
	 * @param dialogImageType
	 *            one of the following values:
	 *            <ul>
	 *            <li><code>MessageDialog.NONE</code> for a dialog with no
	 *            image</li>
	 *            <li><code>MessageDialog.ERROR</code> for a dialog with an
	 *            error image</li>
	 *            <li><code>MessageDialog.INFORMATION</code> for a dialog
	 *            with an information image</li>
	 *            <li><code>MessageDialog.QUESTION </code> for a dialog with a
	 *            question image</li>
	 *            <li><code>MessageDialog.WARNING</code> for a dialog with a
	 *            warning image</li>
	 *            </ul>
	 * @param toggleMessage
	 *            the message for the toggle control, or <code>null</code> for
	 *            the default message
	 * @param toggleState
	 *            the initial state for the toggle
	 * @param input
	 *            the input for the JFace underlying list viewer
	 * @param contentProvider
	 *            the content provider for the JFace underlying list viewer
	 * @param labelProvider
	 *            the label provider for the JFace underlying list viewer
	 */
	public ListSelectionMessageToggleDialog(Shell parentShell,
			String dialogTitle, Image image, String message,
			int dialogImageType, String toggleMessage, boolean toggleState,
			Object input, IStructuredContentProvider contentProvider,
			ILabelProvider labelProvider) {
		this(parentShell, dialogTitle, image, message, dialogImageType,
				new String[] { IDialogConstants.OK_LABEL,
						IDialogConstants.CANCEL_LABEL }, 0, toggleMessage,
				toggleState, input, contentProvider, labelProvider);
	}

	/**
	 * Creates a dialog with toggle for selection in a list
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param dialogTitle
	 *            the dialog title or <code>null</code> if nonde
	 * @param image
	 *            the dialog title image, or <code>null</code> if none
	 * @param message
	 *            the dialog message
	 * @param dialogImageType
	 *            one of the following values:
	 *            <ul>
	 *            <li><code>MessageDialog.NONE</code> for a dialog with no
	 *            image</li>
	 *            <li><code>MessageDialog.ERROR</code> for a dialog with an
	 *            error image</li>
	 *            <li><code>MessageDialog.INFORMATION</code> for a dialog
	 *            with an information image</li>
	 *            <li><code>MessageDialog.QUESTION </code> for a dialog with a
	 *            question image</li>
	 *            <li><code>MessageDialog.WARNING</code> for a dialog with a
	 *            warning image</li>
	 *            </ul>
	 * @param buttonLabels
	 *            an array of labels for the buttons in the button bar
	 * @param defaultIndex
	 *            the index in the button label array of the default button
	 * @param toggleMessage
	 *            the message for the toggle control, or <code>null</code> for
	 *            the default message
	 * @param toggleState
	 *            the initial state for the toggle
	 * @param input
	 *            the input for the JFace underlying list viewer
	 * @param contentProvider
	 *            the content provider for the JFace underlying list viewer
	 * @param labelProvider
	 *            the label provider for the JFace underlying list viewer
	 */
	public ListSelectionMessageToggleDialog(Shell parentShell,
			String dialogTitle, Image image, String message,
			int dialogImageType, String[] buttonLabels, int defaultIndex,
			String toggleMessage, boolean toggleState, Object input,
			IStructuredContentProvider contentProvider,
			ILabelProvider labelProvider) {
		super(parentShell, dialogTitle, image, message, dialogImageType,
				buttonLabels, defaultIndex, toggleMessage, toggleState);

		this.input = input;
		this.contentProvider = contentProvider;
		this.labelProvider = labelProvider;
	}

	/**
	 * Returns the list of selections made by the user, or <code>null</code>
	 * if the selection was canceled.
	 * 
	 * @return the array of selected elements, or <code>null</code> if Cancel
	 *         was pressed
	 */
	public Object[] getResult() {
		return result;
	}

	/**
	 * Sets the initial selection in this selection dialog to the given
	 * elements.
	 * 
	 * @param selectedElements
	 *            the array of elements to select
	 */
	@SuppressWarnings("unchecked")
	public void setInitialSelections(Object[] selectedElements) {
		initialSelections = new ArrayList(selectedElements.length);
		for (int i = 0; i < selectedElements.length; i++) {
			initialSelections.add(selectedElements[i]);
		}
	}

	/**
	 * Sets the initial selection in this selection dialog to the given
	 * elements.
	 * 
	 * @param selectedElements
	 *            the List of elements to select
	 */
	public void setInitialElementSelections(
			List<? extends Object> selectedElements) {
		initialSelections.addAll(selectedElements);
	}

	/**
	 * Add the selection and deselection buttons to the dialog.
	 * 
	 * @param composite
	 *            org.eclipse.swt.widgets.Composite
	 */
	private void addSelectionButtons(Composite composite) {
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(SWT.END, SWT.TOP, true,
				false));

		Button selectButton = createButton(buttonComposite,
				IDialogConstants.SELECT_ALL_ID, SELECT_ALL_LABEL, false);

		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				listViewer.setAllChecked(true);
			}
		};
		selectButton.addSelectionListener(listener);

		Button deselectButton = createButton(buttonComposite,
				IDialogConstants.DESELECT_ALL_ID, DESELECT_ALL_LABEL, false);

		listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				listViewer.setAllChecked(false);
			}
		};
		deselectButton.addSelectionListener(listener);
	}

	/**
	 * Visually checks the previously-specified elements in this dialog's list
	 * viewer.
	 */
	private void checkInitialSelections() {
		Iterator<Object> itemsToCheck = getInitialElementSelections()
				.iterator();

		while (itemsToCheck.hasNext()) {
			listViewer.setChecked(itemsToCheck.next(), true);
		}
	}

	/**
	 * Initializes this dialog's viewer after it has been laid out.
	 */
	private void initializeViewer() {
		listViewer.setInput(input);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case IDialogConstants.DESELECT_ALL_ID:
		case IDialogConstants.SELECT_ALL_ID:
			// Don't close the dialog
			return;
		case IDialogConstants.OK_ID:
			okPressed();
			break;
		default:
			super.buttonPressed(buttonId);
			break;
		}
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		listViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
		data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
		listViewer.getTable().setLayoutData(data);

		listViewer.setLabelProvider(labelProvider);
		listViewer.setContentProvider(contentProvider);

		addSelectionButtons(parent);

		initializeViewer();

		// initialize page
		if (!getInitialElementSelections().isEmpty()) {
			checkInitialSelections();
		}
		return composite;
	}

	/**
	 * Returns the list of initial element selections.
	 * 
	 * @return List
	 */
	protected List<Object> getInitialElementSelections() {
		return initialSelections;
	}

	/**
	 * The <code>ListSelectionDialog</code> implementation of this
	 * <code>Dialog</code> method builds a list of the selected elements for
	 * later retrieval by the client and closes this dialog.
	 */
	@Override
	protected void okPressed() {
		// Get the input children.
		Object[] children = contentProvider.getElements(input);

		// Build a list of selected children.
		if (children != null) {
			ArrayList<Object> list = new ArrayList<Object>();
			for (int i = 0; i < children.length; ++i) {
				Object element = children[i];
				if (listViewer.getChecked(element)) {
					list.add(element);
				}
			}
			setResult(list);
		}
		// Don't forget to set the pref by calling:
		super.buttonPressed(IDialogConstants.OK_ID);
	}

	/**
	 * Set the selections made by the user, or <code>null</code> if the
	 * selection was canceled.
	 * 
	 * @param newResult
	 *            list of selected elements, or <code>null</code> if Cancel
	 *            was pressed
	 */
	protected void setResult(List<Object> newResult) {
		if (newResult == null) {
			result = null;
		} else {
			result = new Object[newResult.size()];
			newResult.toArray(result);
		}
	}
}
