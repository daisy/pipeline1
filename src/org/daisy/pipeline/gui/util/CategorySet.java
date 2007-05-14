package org.daisy.pipeline.gui.util;

import java.util.List;

/**
 * @author Romain Deltour
 *
 */
public abstract class CategorySet {
    private String name;

    public CategorySet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public abstract List<Category> getCategories();
    
    public static CategorySet NONE = new CategorySet("None") {
        @Override
        public List<Category> getCategories() {
            return null;
        }
    };

}
