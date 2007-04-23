package org.daisy.pipeline.gui.util;

public abstract class Category {
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public abstract boolean contains(Object obj);

    public String getName() {
        return name;
    }

}