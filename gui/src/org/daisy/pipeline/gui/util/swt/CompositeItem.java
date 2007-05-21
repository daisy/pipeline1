package org.daisy.pipeline.gui.util.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class CompositeItem extends Composite {

    private CompositeList parentList;
    private boolean selected;

    public CompositeItem(CompositeList parent, int style) {
        this(parent, style, checkNull(parent).getItemCount());
    }

    @SuppressWarnings("unchecked")
    // subclasses are responsible for creating themselves on compatible lists
    public CompositeItem(CompositeList parent, int style, int index) {
        super(parent, style);
        parentList = parent;
        selected = false;
        setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        createChildren();
        parent.itemCreated(this, index);
        hookListeners();
        refreshColors();
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

    protected abstract void createChildren();

    protected void hookListeners() {
        MouseAdapter mouseListener = new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                parentList.setSelection(new int[] { parentList
                        .indexOf(CompositeItem.this) });
            }
        };
        addMouseListener(mouseListener);
        for (Control child : getChildren()) {
            child.addMouseListener(mouseListener);
        }
        addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent event) {
                switch (event.detail) {
                case SWT.TRAVERSE_ARROW_NEXT:
                    parentList.selectNext();
                    break;
                case SWT.TRAVERSE_ARROW_PREVIOUS:
                    parentList.selectPrevious();
                case SWT.TRAVERSE_ESCAPE:
                case SWT.TRAVERSE_RETURN:
                case SWT.TRAVERSE_TAB_NEXT:
                case SWT.TRAVERSE_TAB_PREVIOUS:
                case SWT.TRAVERSE_PAGE_NEXT:
                case SWT.TRAVERSE_PAGE_PREVIOUS:
                default:
                    event.doit = true;
                    break;
                }
            }
        });
    }

    protected void refreshColors() {
        Color background = getDisplay()
                .getSystemColor(
                        selected ? SWT.COLOR_LIST_SELECTION
                                : SWT.COLOR_LIST_BACKGROUND);
        Color foreground = getDisplay().getSystemColor(
                selected ? SWT.COLOR_LIST_SELECTION_TEXT
                        : SWT.COLOR_LIST_FOREGROUND);
        setBackground(background);
        setForeground(foreground);
        for (Control child : getChildren()) {
            child.setBackground(background);
            child.setForeground(foreground);
        }
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
        refreshColors();
    }

}
