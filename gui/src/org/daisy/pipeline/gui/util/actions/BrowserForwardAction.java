package org.daisy.pipeline.gui.util.actions;

import org.daisy.pipeline.gui.GuiPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.ui.ISharedImages;

/**
 * @author Romain Deltour
 * 
 */
public class BrowserForwardAction extends Action {
    private Browser browser;

    public BrowserForwardAction(Browser browser) {
        super();
        this.browser = browser;
        setImageDescriptor(GuiPlugin
                .getSharedDescriptor(ISharedImages.IMG_TOOL_FORWARD));
        setDisabledImageDescriptor(GuiPlugin
                .getSharedDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));
        setText("Forward");
        setToolTipText("Go to the next page");
        setEnabled(browser.isForwardEnabled());
        this.browser.addLocationListener(new LocationAdapter() {
            @Override
            public void changed(LocationEvent event) {
                setEnabled(BrowserForwardAction.this.browser.isForwardEnabled());
            }
        });
    }

    @Override
    public void run() {
        browser.forward();
    }
}
