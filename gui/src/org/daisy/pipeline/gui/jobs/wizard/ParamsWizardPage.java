package org.daisy.pipeline.gui.jobs.wizard;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Script;
import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.DatatypeException;
import org.daisy.pipeline.gui.scripts.datatype.DatatypeAdapter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Romain Deltour
 * 
 */
public class ParamsWizardPage extends WizardPage implements Listener {

    public static final String NAME = "parameters";
    private List<Control> paramControls;
    private boolean isInitializing;

    /**
     * @param pageName
     * @param title
     * @param titleImage
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
        setTitle("Configure the " + script.getNicename());
        setDescription("Configure the parameters of the "
                + script.getNicename());
        // Create controls for required and optional parameters
        ScriptParameter[] reqParams = script.getRequiredParameters().values()
                .toArray(new ScriptParameter[0]);
        ScriptParameter[] optParams = script.getOptionalParameters().values()
                .toArray(new ScriptParameter[0]);
        paramControls = new ArrayList<Control>(reqParams.length
                + optParams.length);
        if (reqParams.length > 0) {
            Group reqGroup = new Group(control, SWT.SHADOW_NONE);
            reqGroup.setText("Required Parameters");
            reqGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
            paramControls.addAll(createParamControls(reqGroup, reqParams));
        }
        if (optParams.length > 0) {
            Group optGroup = new Group(control, SWT.SHADOW_NONE);
            optGroup.setText("Optional Parameters");
            optGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
            paramControls.addAll(createParamControls(optGroup, optParams));
        }
        // Listen to controls modification
        for (Control ctrl : paramControls) {
            ctrl.addListener(SWT.Modify, this);
            ctrl.addListener(SWT.FocusIn, this);
            ctrl.addListener(SWT.FocusOut, this);
        }
        // Init content
        initContent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event) {
        if (isInitializing) {
            return;
        }
        Widget widget = event.widget;
        ScriptParameter param = (ScriptParameter) widget.getData();
        switch (event.type) {
        case SWT.Modify:
            Job job = ((NewJobWizard) getWizard()).getJob();
            DatatypeAdapter adapter = DatatypeAdapter.getAdapter(param
                    .getDatatype());
            String value = adapter.getValue(widget);
            try {
                job.setParameterValue(param.getName(), value);
                setErrorMessage(null);
                updateSettings(param, value);
            } catch (DatatypeException e) {
                setErrorMessage("Invalid parameter: " + e.getLocalizedMessage());
            }
            updatePageComplete(true);
            break;
        case SWT.FocusIn:
            setMessage(param.getDescription(), INFORMATION);
            break;
        case SWT.FocusOut:
            setMessage(null);
            break;
        default:
            break;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            getControl().dispose();
            setControl(null);
        }
    }

    private List<Control> createParamControls(Composite parent,
            ScriptParameter[] params) {
        List<Control> controls = new ArrayList<Control>(params.length);
        DatatypeAdapter adapter;
        // Compute the number of columns
        int numCol = 0;
        for (ScriptParameter param : params) {
            adapter = DatatypeAdapter.getAdapter(param.getDatatype());
            numCol = Math.max(numCol, adapter.getNumCol());
        }
        parent.setLayout(new GridLayout(numCol, false));
        // Create controls for each param
        for (ScriptParameter param : params) {
            adapter = DatatypeAdapter.getAdapter(param.getDatatype());
            controls.add(adapter.createControl(parent, param, numCol));
        }
        return controls;
    }

    private void initContent() {
        isInitializing = true;
        String scriptName = ((NewJobWizard) getWizard()).getJob().getScript()
                .getName();
        IDialogSettings scriptSettings = getDialogSettings().getSection(
                scriptName);
        if (scriptSettings == null) {
            scriptSettings = getDialogSettings().addNewSection(scriptName);
        }
        // Init from default values
        for (Control control : paramControls) {
            ScriptParameter param = (ScriptParameter) control.getData();
            String value;
            if (((NewJobWizard) getWizard()).isFirstInSession()) {
                // init settings with default values
                updateSettings(param, param.getValue());
            }
            value = scriptSettings.get(param.getName());
            if (value != null) {
                DatatypeAdapter adapter = DatatypeAdapter.getAdapter(param
                        .getDatatype());
                adapter.setValue(control, value);
            }
        }
        isInitializing = false;
    }

    private void updateSettings(ScriptParameter param, String value) {
        String scriptName = ((NewJobWizard) getWizard()).getJob().getScript()
                .getName();
        IDialogSettings scriptSettings = getDialogSettings().getSection(
                scriptName);
        scriptSettings.put(param.getName(), value);
    }

    void updatePageComplete(boolean showError) {
        // Check that all required parameters are set
        setPageComplete(false);
        Job job = ((NewJobWizard) getWizard()).getJob();
        if (job.allRequiredParametersSet()) {
            setPageComplete(true);
        }
    }

    @Override
    public void performHelp() {
        ((NewJobWizard) getWizard()).performHelp();
    }

    public class PageControl extends Composite {

        public PageControl(Composite parent, int style) {
            super(parent, style);
        }

        @Override
        public Point computeSize(int wHint, int hHint, boolean changed) {
            Point size = super.computeSize(wHint, hHint, changed);
            Point firstSize = getWizard().getStartingPage().getControl()
                    .getSize();
            size.x = firstSize.x;
            return size;
        }

    }
}
