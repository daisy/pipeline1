package org.daisy.pipeline.gui.messages;

import java.util.LinkedList;
import java.util.List;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.util.CategorySet;
import org.daisy.pipeline.gui.util.Category;

class CauseCategorySet extends CategorySet {
    private List<Category> categories;

    public CauseCategorySet() {
        super("Cause");
        categories = new LinkedList<Category>();
        for (MessageEvent.Cause cause : MessageEvent.Cause.values()) {
            categories.add(new CauseCategory(cause));
        }
    }

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    class CauseCategory extends Category {

        private MessageEvent.Cause cause;

        public CauseCategory(MessageEvent.Cause cause) {
            // TODO localize
            super(cause.toString());
            this.cause = cause;
        }

        @Override
        public boolean contains(Object obj) {
            if (obj instanceof MessageEvent) {
                return ((MessageEvent) obj).getCause() == cause;
            }
            return false;
        }

        public MessageEvent.Cause getCause() {
            return cause;
        }

    }
}