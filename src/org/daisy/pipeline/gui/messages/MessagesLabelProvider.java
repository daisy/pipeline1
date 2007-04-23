package org.daisy.pipeline.gui.messages;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.util.Category;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Romain Deltour
 * 
 */
public class MessagesLabelProvider extends LabelProvider implements
        ILabelProvider {

    @Override
    public Image getImage(Object element) {
        return super.getImage(element);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof MessageEvent) {
            return ((MessageEvent) element).getMessage();
        }
        if (element instanceof Category) {
            return ((Category) element).getName();
        }
        return super.getText(element);
    }

}
