package org.daisy.pipeline.gui.messages;

import javax.xml.stream.Location;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.util.AbstractTableField;
import org.daisy.pipeline.gui.util.Category;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
class MessageField extends AbstractTableField {

    @Override
    public Image getImage(Object element) {
        if (element instanceof MessageEvent) {
            switch ((((MessageEvent) element).getType())) {
            case DEBUG:
                return GuiPlugin.getImage(IIconsKeys.MESSAGE_DEBUG);
            case ERROR:
                return GuiPlugin.getImage(IIconsKeys.MESSAGE_ERROR);
            case INFO:
                return GuiPlugin.getImage(IIconsKeys.MESSAGE_INFO);
            case WARNING:
                return GuiPlugin.getImage(IIconsKeys.MESSAGE_WARNING);
            }
        }
        if (element instanceof Category) {
            return GuiPlugin.getImage(IIconsKeys.TREE_CATEGORY);
        }
        return null;
    }

    @Override
    public String getHeaderText() {
        return "Message";
    }

    @Override
    public String getText(Object element) {
        if (element instanceof MessageEvent) {
            return ((MessageEvent) element).getMessage();
        }
        if (element instanceof Category) {
            return ((Category) element).getName();
        }
        if (element instanceof Location) {
            Location loc = (Location) element;
            return loc.toString();
        }
        return super.getText(element);
    }

    @Override
    public int getWeight() {
        return 5;
    }

}
