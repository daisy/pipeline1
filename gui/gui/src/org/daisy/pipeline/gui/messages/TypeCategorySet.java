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

import java.util.LinkedList;
import java.util.List;

import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.gui.util.CategorySet;
import org.daisy.pipeline.gui.util.Category;

class TypeCategorySet extends CategorySet {
    private List<Category> categories;

    public TypeCategorySet() {
        super(Messages.groupBy_categorySet_type);
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
            super(Messages.getName(type));
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