package org.daisy.pipeline.gui.messages;

import java.util.LinkedList;
import java.util.List;

import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.pipeline.gui.util.CategorySet;
import org.daisy.pipeline.gui.util.Category;

class TypeCategorySet extends CategorySet {
    private List<Category> categories;

    public TypeCategorySet() {
        super("Type");
        categories = new LinkedList<Category>();
        for (MessageEvent.Type type : MessageEvent.Type.values()) {
            categories.add(new TypeCategory(type));
        }
    }

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    class TypeCategory extends Category {

        private MessageEvent.Type type;

        public TypeCategory(MessageEvent.Type type) {
            // TODO localize
            super(type.toString());
            this.type = type;
        }

        @Override
        public boolean contains(Object obj) {
            if (obj instanceof MessageEvent) {
                return ((MessageEvent) obj).getType() == type;
            }
            return false;
        }

        public MessageEvent.Type getType() {
            return type;
        }

    }
}