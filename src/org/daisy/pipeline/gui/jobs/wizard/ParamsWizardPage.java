package org.daisy.pipeline.gui.jobs.wizard;

import java.util.Collection;

import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Script;
import org.daisy.dmfc.core.script.ScriptParameter;
import org.daisy.dmfc.core.script.datatype.DatatypeException;
import org.daisy.pipeline.gui.scripts.DatatypeHelper;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Romain Deltour
 * 
 */
public class ParamsWizardPage extends WizardPage implements Listener {

    public static final String NAME = "parameters";

    /**
     * @param pageName
     * @param title
     * @param titleImage
     */
    protected ParamsWizardPage() {
        super(NAME);
        setTitle("Configure Job");
        setDescription("Configure the parameters of the new job.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite control = new Composite(parent, SWT.NULL);
        control.setLayout(new GridLayout(1, true));
        setControl(control);
        Script script = ((NewJobWizard) getWizard()).getJob().getScript();
        // Create group of required parameters
        Group reqGroup = new Group(control, SWT.SHADOW_NONE);
        reqGroup.setText("Required Parameters");
        reqGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        createParamControls(reqGroup, script, true);
        // Create group of optional parameters
        Group optGroup = new Group(control, SWT.SHADOW_NONE);
        optGroup.setText("Optional Parameters");
        optGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        createParamControls(optGroup, script, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event) {
        // TODO queue wizard messages for better user experience
        Widget widget = event.widget;
        ScriptParameter param = (ScriptParameter) widget.getData();
        switch (event.type) {
        case SWT.Modify:
            Job job = ((NewJobWizard) getWizard()).getJob();
            String value = DatatypeHelper.getValue(widget, param);
            try {
                job.setParameterValue(param.getName(), value);
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
        if (!visible) {
            getControl().dispose();
            setControl(null);
        }
        super.setVisible(visible);
    }

    private void createParamControls(Composite parent, Script script,
            boolean required) {
        Collection<ScriptParameter> params;
        if (required) {
            params = script.getRequiredParameters().values();
        } else {
            params = script.getOptionalParameters().values();
        }
        // Compute the number of columns
        int numCol = 0;
        for (ScriptParameter param : params) {
            numCol = Math.max(numCol, DatatypeHelper.getNumColumns(param
                    .getDatatype()));
        }
        parent.setLayout(new GridLayout(numCol + 1, false));
        // Create controls for each param
        for (ScriptParameter param : params) {
            Label label = new Label(parent, SWT.NONE);
            label.setText(param.getNicename());
            label.setToolTipText(param.getDescription());
            GridData data = new GridData();
            data.grabExcessVerticalSpace = true;
            data.verticalAlignment = GridData.CENTER;
            data.horizontalAlignment = GridData.END;
            label.setLayoutData(data);
            DatatypeHelper.createControl(parent, param, this, numCol);
        }
    }

    void updatePageComplete(boolean showError) {
        setPageComplete(false);

        // Check that all required parameters are set
        Job job = ((NewJobWizard) getWizard()).getJob();
        if (job.allRequiredParametersSet()) {
            setErrorMessage(null);
            setPageComplete(true);
        }
    }

    @Override
    public void performHelp() {
        ((NewJobWizard) getWizard()).performHelp();
    }
}
