package org.daisy.pipeline.gui.messages;

import java.util.EnumSet;

import org.daisy.dmfc.core.event.MessageEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Romain Deltour
 * 
 */
public class MessageFilter extends ViewerFilter {
    EnumSet<MessageEvent.Cause> causes;
    EnumSet<MessageEvent.Type> types;

    public MessageFilter() {
        causes = EnumSet.allOf(MessageEvent.Cause.class);
        types = EnumSet.allOf(MessageEvent.Type.class);
    }

    public void configure(MessageEvent.Cause cause, boolean select) {
        if (select) {
            causes.add(cause);
        } else {
            causes.remove(cause);
        }
    }

    public void configure(MessageEvent.Type type, boolean select) {
        if (select) {
            types.add(type);
        } else {
            types.remove(type);
        }
    }

    public boolean isAccepted(MessageEvent.Type type) {
        return types.contains(type);
    }

    public boolean isAccepted(MessageEvent.Cause cause) {
        return causes.contains(cause);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof MessageEvent) {
            MessageEvent me = (MessageEvent) element;
            return causes.contains(me.getCause())
                    && types.contains(me.getType());
        }
        return true;
    }

}
