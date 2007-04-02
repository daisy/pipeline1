package org.daisy.pipeline.gui.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public abstract class CompositeItem extends Composite {

    public CompositeItem(CompositeList parent, int style) {
        this(parent, style, checkNull(parent).getItemCount());
    }

    @SuppressWarnings("unchecked")
    // subclasses are responsible for creating themselves on compatible lists
    public CompositeItem(CompositeList parent, int style, int index) {
        super(parent, style);
        parent.itemCreated(this, index);
    }

    private static CompositeList checkNull(CompositeList control) {
        if (control == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        return control;
    }
    public void refresh() {
        if (isDisposed()) {
            return;
        }
    }

    public void setImage(String key, Image image) {
    }

    public void setInt(String key, int value) {
    }

    public void setText(String key, String text) {
    }
}
