/*
 * DAISY Pipeline GUI
 * Copyright (C) 2006  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.daisy.pipeline.gui.messages;

import java.util.EnumSet;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.gui.messages.CauseCategorySet.CauseCategory;
import org.daisy.pipeline.gui.messages.TypeCategorySet.TypeCategory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * @author Romain Deltour
 * 
 */
public class MessageFilter extends ViewerFilter {
    private static final String TAG_SECTION = "FilterInfo"; //$NON-NLS-1$
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
        if (element instanceof TypeCategory) {
            return types.contains(((TypeCategory) element).getType());
        }
        if (element instanceof CauseCategory) {
            return causes.contains(((CauseCategory) element).getCause());
        }
        return true;
    }

    public void saveState(IMemento memento) {
        IMemento mem = memento.createChild(TAG_SECTION);
        for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
            mem.putInteger(cause.name(), isAccepted(cause) ? 1 : 0);
        }
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            mem.putInteger(type.name(), isAccepted(type) ? 1 : 0);
        }
    }

    public void init(IMemento memento) {
        IMemento mem = (memento != null) ? memento.getChild(TAG_SECTION) : null;
        if (mem == null) {
            mem = getDefaults();
        }
        for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
            Integer accepted = mem.getInteger(cause.name());
            configure(cause, accepted == null || accepted == 1);
        }
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            Integer accepted = mem.getInteger(type.name());
            configure(type, accepted == null || accepted == 1);
        }
    }

    private IMemento getDefaults() {
        XMLMemento memento = XMLMemento.createWriteRoot(TAG_SECTION);
        for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
            memento.putInteger(cause.name(), 1);
        }
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            memento.putInteger(type.name(),
                    (type == MessageEvent.Type.DEBUG) ? 0 : 1);
        }
        return memento;
    }

}