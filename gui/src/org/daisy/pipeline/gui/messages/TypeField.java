package org.daisy.pipeline.gui.messages;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.util.AbstractTableField;

/**
 * @author Romain Deltour
 * 
 */
public class TypeField extends AbstractTableField {

    @Override
    public String getHeaderText() {
        return Messages.heading_type;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof MessageEvent) {
            return ((MessageEvent) element).getType().toString();
        }
        return null;
    }

    @Override
    public int getWeight() {
        return 1;
    }

}
