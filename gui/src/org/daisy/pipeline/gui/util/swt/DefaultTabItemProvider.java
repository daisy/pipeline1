package org.daisy.pipeline.gui.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author Romain Deltour
 * 
 */
public abstract class DefaultTabItemProvider implements ITabItemProvider {

    private TabItem item;

    public DefaultTabItemProvider() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.pipeline.gui.doc.ITabITemProvider#createTabItem(org.eclipse.swt.widgets.TabFolder)
     */
    public TabItem createTabItem(TabFolder parent) {
        item = new TabItem(parent, SWT.NONE);
        item.setText(getTitle());
        item.setToolTipText(getToolTipText());
        item.setControl(createControl(parent));
        return item;
    }

    protected abstract Control createControl(TabFolder parent);

    public TabItem getTabItem() {
        return item;
    }

    protected abstract String getTitle();

    protected abstract String getToolTipText();

}