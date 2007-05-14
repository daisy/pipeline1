package org.daisy.pipeline.gui.util;

public abstract class Category {
    private String name;
    private boolean isVisible;

    public Category(String name) {
        this.name = name;
        this.isVisible = false;
    }

    public abstract boolean contains(Object obj);

    public String getName() {
        return name;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

}