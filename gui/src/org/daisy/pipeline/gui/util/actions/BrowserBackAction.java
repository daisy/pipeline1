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
public class BrowserBackAction extends Action {
    private final Browser browser;

    public BrowserBackAction(Browser browser) {
        super();
        this.browser = browser;
        setImageDescriptor(GuiPlugin
                .getSharedDescriptor(ISharedImages.IMG_TOOL_BACK));
        setDisabledImageDescriptor(GuiPlugin
                .getSharedDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));
        setText("Back");
        setToolTipText("Go to the previous page");
        setEnabled(browser.isBackEnabled());
        this.browser.addLocationListener(new LocationAdapter() {
            @Override
            public void changed(LocationEvent event) {
                setEnabled(BrowserBackAction.this.browser.isBackEnabled());
            }
        });
    }

    @Override
    public void run() {
        browser.back();
    }
}
