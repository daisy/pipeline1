package org.daisy.pipeline.gui.doc;

import java.io.File;

import org.daisy.dmfc.core.script.Script;
import org.daisy.pipeline.gui.scripts.ScriptManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Romain Deltour
 * 
 */
public class DocView extends ViewPart implements ISelectionListener {

    public static final String ID = "org.daisy.pipeline.gui.views.doc";

    private Browser browser;
    private IWorkbenchWindow window;

    public DocView() {
        super();
    }

    @Override
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        window = site.getWorkbenchWindow();
        window.getSelectionService().addSelectionListener(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (window != null) {
            window.getSelectionService().removeSelectionListener(this);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        browser = new Browser(parent, SWT.NONE);
    }

    @Override
    public void setFocus() {
        // TODO set the focus
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            if (obj instanceof File) {
                File file = (File) obj;
                if (file.isFile()) {
                    Script script = ScriptManager.getDefault().getScript(
                            file.getPath());
                    browser.setUrl(script.getDocumentation().toString());
                }
            }
        }

    }

}
