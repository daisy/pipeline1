package org.daisy.pipeline.gui.messages;

import javax.xml.stream.Location;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.util.AbstractTableField;
import org.daisy.pipeline.gui.util.Category;

/**
 * @author Romain Deltour
 * 
 */
class MessageField extends AbstractTableField {

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
