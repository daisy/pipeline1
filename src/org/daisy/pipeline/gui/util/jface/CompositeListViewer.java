package org.daisy.pipeline.gui.util.jface;

import java.util.ArrayList;
import java.util.List;

import org.daisy.pipeline.gui.util.swt.CompositeItem;
import org.daisy.pipeline.gui.util.swt.CompositeList;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

public abstract class CompositeListViewer<I extends CompositeItem> extends
        StructuredViewer {

    private CompositeList<I> control;

    /**
     * Creates a list viewer on the given list control. The viewer has no input,
     * no content provider, a default label provider, no sorter, and no filters.
     * 
     * @param list the list control
     */
    public CompositeListViewer(CompositeList<I> list) {
        this.control = list;
        setLabelProvider(new CompositeLabelProvider());
        hookControl(list);
    }

    @Override
    public Control getControl() {
        return control;
    }

    /**
     * Returns the element with the given index from this list viewer. Returns
     * <code>null</code> if the index is out of range.
     * 
     * @param index the zero-based index
     * @return the element at the given index, or <code>null</code> if the
     *         index is out of range
     */
    public Object getElementAt(int index) {

        if (index >= 0 && index < control.getItemCount()) {
            CompositeItem i = control.getItem(index);
            if (i != null) {
                return i.getData();
            }
        }
        return null;
    }

    /**
     * The composite list viewer implementation of this <code>Viewer</code>
     * framework method returns the label provider, which will be an instance of
     * <code>ICompositeLabelProvider</code>
     */
    @Override
    public IBaseLabelProvider getLabelProvider() {
        return super.getLabelProvider();
    }

    @Override
    public void reveal(Object element) {
        Assert.isNotNull(element);
        Widget w = findItem(element);
        if (w instanceof CompositeItem) {
            control.showItem((CompositeItem) w);
        }
    }

    /**
     * The composite list viewer implementation of this <code>Viewer</code>
     * framework method ensures that the given label provider is an instance of
     * <code>ICompositeLabelProvider</code>.
     */
    @Override
    public void setLabelProvider(IBaseLabelProvider labelProvider) {
        Assert.isTrue(labelProvider instanceof ICompositeLabelProvider);
        super.setLabelProvider(labelProvider);
    }

    private void internalRefreshAll(boolean updateLabels) {

        // in the code below, it is important to do all disassociates
        // before any associates, since a later disassociate can undo an
        // earlier associate
        // e.g. if (a, b) is replaced by (b, a), the disassociate of b to
        // item 1 could undo
        // the associate of b to item 0.

        Object[] children = getSortedChildren(getRoot());
        CompositeItem[] items = control.getItems();
        int min = Math.min(children.length, items.length);
        for (int i = 0; i < min; ++i) {

            CompositeItem item = items[i];

            // if the element is unchanged, update its label if appropriate
            if (equals(children[i], item.getData())) {
                if (updateLabels) {
                    updateItem(item, children[i]);
                } else {
                    // associate the new element, even if equal to the old
                    // one,
                    // to remove stale references (see bug 31314)
                    associate(children[i], item);
                }
            } else {
                // updateItem does an associate(...), which can mess up
                // the associations if the order of elements has changed.
                // E.g. (a, b) -> (b, a) first replaces a->0 with b->0, then
                // replaces b->1 with a->1, but this actually removes b->0.
                // So, if the object associated with this item has changed,
                // just disassociate it for now, and update it below.

                // item.setText(""); //$NON-NLS-1$
                // item.setImage(new
                // Image[Math.max(1,table.getColumnCount())]);//Clear all images
                disassociate(item);
            }
        }
        // dispose of all items beyond the end of the current elements
        if (min < items.length) {
            for (int i = items.length; --i >= min;) {

                disassociate(items[i]);
            }
            control.remove(min, items.length - 1);
        }
        if (control.getItemCount() == 0) {
            control.removeAll();
        }
        // Update items which were removed above
        for (int i = 0; i < min; ++i) {

            CompositeItem item = items[i];
            if (item.getData() == null) {
                updateItem(item, children[i]);
            }
        }
        // add any remaining elements
        for (int i = min; i < children.length; ++i) {
            updateItem(createItem(control, i), children[i]);
        }
    }

    @Override
    protected void assertContentProviderType(IContentProvider provider) {
        Assert.isTrue(provider instanceof IStructuredContentProvider);
    }

    // @see StructuredViewer.associate(Object,Item)
    protected void associate(Object element, CompositeItem item) {
        Object data = item.getData();
        if (data != element) {
            if (data != null) {
                disassociate(item);
            }
            item.setData(element);
        }
        mapElement(element, item);
    }

    protected abstract I createItem(CompositeList<I> parent, int index);

    // @see StructuredViewer.disassociate(Item)
    protected void disassociate(CompositeItem item) {
        Object element = item.getData();
        Assert.isNotNull(element);
        // Clear the map before we clear the data
        unmapElement(element, item);
        item.setData(null);
    }

    @Override
    protected Widget doFindInputItem(Object element) {
        if (element != null && equals(element, getRoot())) {
            return getControl();
        }
        return null;
    }

    @Override
    protected Widget doFindItem(Object element) {
        CompositeItem[] children = control.getItems();
        for (int i = 0; i < children.length; i++) {
            CompositeItem item = children[i];
            Object data = item.getData();
            if (data != null && equals(data, element)) {
                return item;
            }
        }
        return null;
    }

    @Override
    protected void doUpdateItem(Widget widget, Object element, boolean fullMap) {
        if (!(widget instanceof CompositeItem)) {
            return;
        }
        final CompositeItem item = (CompositeItem) widget;

        // remember element we are showing
        if (fullMap) {
            associate(element, item);
        } else {
            Object data = item.getData();
            if (data != null) {
                unmapElement(data, item);
            }
            item.setData(element);
            mapElement(element, item);
        }
        IBaseLabelProvider prov = getLabelProvider();
        if (prov != null && prov instanceof ICompositeLabelProvider) {
            ICompositeLabelProvider cprov = (ICompositeLabelProvider) getLabelProvider();
            for (String key : cprov.getImageKeys()) {
                Image image = cprov.getImage(key, element);
                item.setImage(key, image);
            }
            for (String key : cprov.getIntKeys()) {
                int value = cprov.getInt(key, element);
                item.setInt(key, value);
            }
            for (String key : cprov.getTextKeys()) {
                String text = cprov.getText(key, element);
                item.setText(key, text != null ? text : "");
            }
        }
        item.refresh();
    }

    @Override
    protected List<Object> getSelectionFromWidget() {
        int[] ixs = control.getSelectionIndices();
        ArrayList<Object> list = new ArrayList<Object>(ixs.length);
        for (int i = 0; i < ixs.length; i++) {
            Object e = getElementAt(ixs[i]);
            if (e != null) {
                list.add(e);
            }
        }
        return list;
    }

    @Override
    protected void inputChanged(Object input, Object oldInput) {
        getControl().setRedraw(false);
        try {
            // refresh() attempts to preserve selection, which we want here
            refresh();
        } finally {
            getControl().setRedraw(true);
        }
    }

    @Override
    protected void internalRefresh(Object element) {
        internalRefresh(element, true);
    }

    @Override
    protected void internalRefresh(Object element, boolean updateLabels) {
        if (element == null || equals(element, getRoot())) {
            internalRefreshAll(updateLabels);
        } else {
            Widget w = findItem(element);
            if (w != null) {
                updateItem(w, element);
            }
        }
    }

    @Override
    protected void setSelectionToWidget(List sel, boolean reveal) {
        if (sel == null || sel.size() == 0) { // clear selection
            control.deselectAll();// calls list.deselectAll
        } else {
            int size = sel.size();
            int[] ixs = new int[size];
            int count = 0;
            for (int i = 0; i < size; ++i) {
                Object o = sel.get(i);
                Widget w = findItem(o);
                if (w instanceof CompositeItem) {
                    int ix = control.indexOf((CompositeItem) w);
                    if (ix >= 0) {
                        ixs[count++] = ix;
                    }
                }
            }
            if (count < size) {
                System.arraycopy(ixs, 0, ixs = new int[count], 0, count);
            }
            control.setSelection(ixs);
            if (reveal) {
                control.showSelection();
            }
        }
    }
}
