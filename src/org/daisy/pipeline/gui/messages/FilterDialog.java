package org.daisy.pipeline.gui.messages;

import org.daisy.dmfc.core.event.MessageEvent;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Romain Deltour
 * 
 */
public class FilterDialog extends TrayDialog {

    private CheckboxTableViewer causes;
    private CheckboxTableViewer types;
    private MessageFilter filter;

    /**
     * @param shell
     */
    protected FilterDialog(Shell shell, MessageFilter filter) {
        super(shell);
        this.filter = filter;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Filter");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        createCauseGroup(area);
        createSeparatorLine(area);
        createTypeGroup(area);
        createSeparatorLine(area);
        applyDialogFont(area);
        return area;
    }

    @Override
    protected void okPressed() {
        /**
         * Updates the filter from the UI state. Must be done here rather than
         * by extending open() because after super.open() is called, the
         * widgetry is disposed.
         */
        for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
            filter.configure(cause, causes.getChecked(cause));
        }
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            filter.configure(type, types.getChecked(type));
        }
        super.okPressed();
    }

    private void createTypeGroup(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Severity:");
        types = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
        types.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        types.setContentProvider(new ArrayContentProvider());
        types.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return element.toString();
            }
        });
        types.setInput(MessageEvent.Type.values());
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            types.setChecked(type, filter.isAccepted(type));
        }
        createSelectButtons(parent, types);
    }

    private void createCauseGroup(Composite parent) {

        Label label = new Label(parent, SWT.NONE);
        label.setText("Types:");
        causes = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
        causes.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        causes.setContentProvider(new ArrayContentProvider());
        causes.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return element.toString();
            }
        });
        causes.setInput(MessageEvent.Cause.values());
        for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
            causes.setChecked(cause, filter.isAccepted(cause));
        }
        createSelectButtons(parent, causes);

        // TODO remove after design decision
        // Group group = new Group(parent, SWT.SHADOW_NONE);
        // group.setText("Severity");
        // group.setLayout(new GridLayout(2, true));
        // final List<Button> buttons = new ArrayList<Button>();
        // for (MessageEvent.Type type : MessageEvent.Type.values()) {
        // Button button = new Button(group, SWT.CHECK);
        // button.setText(type.toString());
        // buttons.add(button);
        // GridData data = new GridData();
        // data.horizontalSpan = 2;
        // button.setLayoutData(data);
        // }
        // // createButton(group, SELECT_ALL_CAUSE_ID, "select all", false);
        // Button selectAll = new Button(group, SWT.PUSH);
        // selectAll.setText("Select All");
        // selectAll.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // for (Button button : buttons) {
        // button.setSelection(true);
        // }
        // }
        // });
        // Button deselectAll = new Button(group, SWT.PUSH);
        // deselectAll.setText("Deselect All");
        // deselectAll.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // for (Button button : buttons) {
        // button.setSelection(false);
        // }
        // }
        // });
    }

    private void createSelectButtons(Composite parent,
            final CheckboxTableViewer viewer) {
        Composite buttons = new Composite(parent, SWT.NONE);
        buttons.setLayout(new GridLayout(2, true));
        Button selectAll = new Button(buttons, SWT.PUSH);
        selectAll.setText("Select All");
        selectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                viewer.setAllChecked(true);
            }
        });
        Button deselectAll = new Button(buttons, SWT.PUSH);
        deselectAll.setText("Deselect All");
        deselectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                viewer.setAllChecked(false);
            }
        });

    }

    private void createSeparatorLine(Composite parent) {
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }
}
