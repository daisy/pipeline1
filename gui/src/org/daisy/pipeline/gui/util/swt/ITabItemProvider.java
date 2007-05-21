package org.daisy.pipeline.gui.util.swt;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/**
 * @author Romain Deltour
 * 
 */
public interface ITabItemProvider {

    public TabItem createTabItem(TabFolder parent);

    public TabItem getTabItem();

}