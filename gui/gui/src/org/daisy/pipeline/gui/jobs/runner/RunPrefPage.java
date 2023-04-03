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
package org.daisy.pipeline.gui.jobs.runner;

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.jobs.Messages;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Romain Deltour
 * 
 */
public class RunPrefPage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {
    public static final String ID = "org.daisy.pipeline.gui.prefPage.run"; //$NON-NLS-1$
    private FieldEditor abortedEditor;
    private FieldEditor failedEditor;
    private FieldEditor finishedEditor;

    public RunPrefPage() {
        super(GRID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        // Nothing to do
    }

    @Override
    protected void createFieldEditors() {
        abortedEditor = new RadioGroupFieldEditor(
                RunJobsStateChecker.ALWAYS_RUN_ABORTED_PREF,
                Messages.prefPage_run_aborted_label, 1, new String[][] {
                        { Messages.prefPage_run_always, MessageDialogWithToggle.ALWAYS },
                        { Messages.prefPage_run_prompt, MessageDialogWithToggle.PROMPT } },
                getFieldEditorParent());
        addField(abortedEditor);
        failedEditor = new RadioGroupFieldEditor(
                RunJobsStateChecker.ALWAYS_RUN_FAILED_PREF,
                Messages.prefPage_run_failed_label, 1, new String[][] {
                        { Messages.prefPage_run_always, MessageDialogWithToggle.ALWAYS },
                        { Messages.prefPage_run_prompt, MessageDialogWithToggle.PROMPT } },
                getFieldEditorParent());
        addField(failedEditor);
        finishedEditor = new RadioGroupFieldEditor(
                RunJobsStateChecker.ALWAYS_RUN_FINISHED_PREF,
                Messages.prefPage_run_finished_label, 1, new String[][] {
                        { Messages.prefPage_run_always, MessageDialogWithToggle.ALWAYS },
                        { Messages.prefPage_run_prompt, MessageDialogWithToggle.PROMPT } },
                getFieldEditorParent());
        addField(finishedEditor);
    }

    @Override
    protected void initialize() {
        super.initialize();
        abortedEditor.load();
        failedEditor.load();
        finishedEditor.load();
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return GuiPlugin.get().getPreferenceStore();
    }

}
