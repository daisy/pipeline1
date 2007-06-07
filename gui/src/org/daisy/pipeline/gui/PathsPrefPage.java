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
package org.daisy.pipeline.gui;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author Romain Deltour
 * 
 */
public class PathsPrefPage extends FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public PathsPrefPage() {
        super(GRID);
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        // Lame Path
        FileFieldEditor lameEditor = new FileFieldEditor(
                PipelineUtil.PATH_TO_LAME, "LAME executable:", parent);
        lameEditor.getTextControl(parent).setToolTipText(
                "Path to the LAME MP3 encoder executable");
        addField(lameEditor);
        // Python Path
        FileFieldEditor pythonEditor = new FileFieldEditor(
                PipelineUtil.PATH_TO_PYTHON, "Python executable:", parent);
        pythonEditor.getTextControl(parent).setToolTipText(
                "Path to the Python executable");
        addField(pythonEditor);
        // Temp Dir Path
        DirectoryFieldEditor tempDirEditor = new DirectoryFieldEditor(
                PipelineUtil.PATH_TO_TEMP_DIR, "Temporary Directory:", parent);
        tempDirEditor.getTextControl(parent).setToolTipText(
                "Path to the temporary directory");
        addField(tempDirEditor);
    }

    @Override
    protected IPreferenceStore doGetPreferenceStore() {
        return new ScopedPreferenceStore(new ConfigurationScope(), GuiPlugin.ID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        // Nothing
    }

    @Override
    public boolean performOk() {
        boolean res = super.performOk();
        GuiPlugin.get().getCore().setUserProperties(
                PipelineUtil.convPrefToProperties());
        return res;
    }

}
