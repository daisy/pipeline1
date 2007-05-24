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
 * @author Romain Deltour
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
    private List initialSelections = new ArrayList();

    // providers for populating this dialog
    private ILabelProvider labelProvider;

    private IStructuredContentProvider contentProvider;

    // the visual selection widget group
    CheckboxTableViewer listViewer;

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
     * @param selectedElements the array of elements to select
     */
    @SuppressWarnings("unchecked") //$NON-NLS-1$
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
     * @param selectedElements the List of elements to select
     */
    public void setInitialElementSelections(List selectedElements) {
        initialSelections = selectedElements;
    }

    /**
     * Add the selection and deselection buttons to the dialog.
     * 
     * @param composite org.eclipse.swt.widgets.Composite
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
        Iterator itemsToCheck = getInitialElementSelections().iterator();

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
    protected List getInitialElementSelections() {
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
    }

    /**
     * Set the selections made by the user, or <code>null</code> if the
     * selection was canceled.
     * 
     * @param newResult list of selected elements, or <code>null</code> if
     *            Cancel was pressed
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
