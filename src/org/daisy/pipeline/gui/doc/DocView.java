package org.daisy.pipeline.gui.doc;

import java.net.URL;

import org.daisy.pipeline.gui.PipelineGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Romain Deltour
 * 
 */
public class DocView extends ViewPart {

    public static final String ID = "org.daisy.pipeline.gui.views.doc";

    public DocView() {
        super();
    }

    @Override
    public void createPartControl(Composite parent) {
        Browser browser = new Browser(parent, SWT.NONE);
        URL url = PipelineGuiPlugin.getResourceURL("./index.html");
        browser.setUrl(url.toString());
    }

    @Override
    public void setFocus() {
        // TODO set the focus
    }

}
