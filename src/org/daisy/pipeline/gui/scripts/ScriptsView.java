package org.daisy.pipeline.gui.scripts;

import org.daisy.pipeline.gui.util.jface.FileTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Romain Deltour
 * 
 */
public class ScriptsView extends ViewPart {

    public static final String ID = "org.daisy.pipeline.gui.views.scripts";
    private ScriptManager scriptMan;
    private TreeViewer scriptViewer;

    public ScriptsView() {
        super();
        scriptMan = ScriptManager.getDefault();
    }

    @Override
    public void createPartControl(Composite parent) {
        // Tree of script files
        scriptViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.BORDER);
        scriptViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        scriptViewer.setContentProvider(new FileTreeContentProvider(
                new ScriptFileFilter()));
        scriptViewer.setLabelProvider(new ScriptsLabelProvider());
        scriptViewer.setInput(scriptMan.getScriptDir());
        // Make the script tree the selection provider
        getSite().setSelectionProvider(scriptViewer);
    }

    @Override
    public void setFocus() {
        scriptViewer.getControl().setFocus();
    }

}
