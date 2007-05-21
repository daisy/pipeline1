package org.daisy.pipeline.gui.util.swt;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("unchecked")
public class CompositeList<I extends CompositeItem> extends Composite {
    private int itemCount = 0;
    private int[] selection = new int[4];
    private I[] items = (I[]) new CompositeItem[4];

    public CompositeList(Composite parent, int style) {
        super(parent, style);
        GridLayout layout = new GridLayout(1, true);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.verticalSpacing = 0;

        setBackground(parent.getDisplay().getSystemColor(
                SWT.COLOR_LIST_BACKGROUND));
        setLayout(layout);
        hookListeners();
    }

    /**
     * Deselects all selected items in the receiver.
     * 
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public void deselectAll() {
        checkWidget();
        deselect(null);
    }

    /**
     * Returns the item at the given, zero-relative index in the receiver.
     * Throws an exception if the index is out of range.
     * 
     * @param index the index of the item to return
     * @return the item at the given index
     * 
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_INVALID_RANGE - if the index is not between 0
     *                and the number of elements in the list minus 1 (inclusive)</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public I getItem(int index) {
        checkWidget();
        if (!(0 <= index && index < itemCount)) {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        return items[index];
    }

    /**
     * Returns the number of items contained in the receiver.
     * 
     * @return the number of items
     * 
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public int getItemCount() {
        checkWidget();
        return itemCount;
    }

    /**
     * Returns a (possibly empty) array of <code>I</code>s which are the
     * items in the receiver.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its list of items, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return the items in the receiver
     * 
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public I[] getItems() {
        checkWidget();
        I[] result = (I[]) new CompositeItem[itemCount];
        System.arraycopy(items, 0, result, 0, itemCount);
        return result;
    }

    /**
     * Returns the zero-relative ordered indices of the items which are
     * currently selected in the receiver. The array is empty if no items are
     * selected.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain
     * its selection, so modifying the array will not affect the receiver.
     * </p>
     * 
     * @return the array of indices of the selected items
     * 
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public int[] getSelectionIndices() {
        checkWidget();
        int[] result = new int[selection.length];
        System.arraycopy(selection, 0, result, 0, selection.length);
        return result;
    }

    /**
     * Searches the receiver's list starting at the first item (index 0) until
     * an item is found that is equal to the argument, and returns the index of
     * that item. If no item is found, returns -1.
     * 
     * @param item the search item
     * @return the index of the item
     * 
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public int indexOf(CompositeItem item) {
        checkWidget();
        if (item == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        for (int i = 0; i < itemCount; i++) {
            if (items[i] == item) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes the item from the receiver at the given zero-relative index.
     * 
     * @param index the index for the item
     * 
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_INVALID_RANGE - if the index is not between 0
     *                and the number of elements in the list minus 1 (inclusive)</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public void remove(int index) {
        checkWidget();
        if (!(0 <= index && index < itemCount)) {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        items[index].dispose();
        if (index != itemCount - 1) {
            fixSelection(index, false);
        }
        System.arraycopy(items, index + 1, items, index, --itemCount - index);
        items[itemCount] = null;
        if (itemCount == 0) {
            items = (I[]) new CompositeItem[4];
        }
    }

    /**
     * Removes the items from the receiver which are between the given
     * zero-relative start and end indices (inclusive).
     * 
     * @param start the start of the range
     * @param end the end of the range
     * 
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_INVALID_RANGE - if either the start or end are
     *                not between 0 and the number of elements in the list minus
     *                1 (inclusive)</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public void remove(int start, int end) {
        checkWidget();
        if (start > end)
            return;
        if (!(0 <= start && start <= end && end < itemCount)) {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        if (start == 0 && end == itemCount - 1) {
            removeAll();
        } else {
            int length = end - start + 1;
            for (int i = 0; i < length; i++)
                remove(start);
        }
    }

    /**
     * Removes all of the items from the receiver.
     * 
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     */
    public void removeAll() {
        checkWidget();
        for (int i = 0; i < itemCount; i++) {
            I item = items[i];
            if (item != null && !item.isDisposed())
                item.dispose();
        }
        itemCount = 0;
        items = (I[]) new CompositeItem[4];
    }

    /**
     * Selects the items at the given zero-relative indices in the receiver. The
     * current selection is cleared before the new items are selected.
     * <p>
     * Indices that are out of range and duplicate indices are ignored. If the
     * receiver is single-select and multiple indices are specified, then all
     * indices are ignored.
     * </p>
     * 
     * @param indices the indices of the items to select
     * 
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the array of indices is null</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     * 
     * @see CompositeList#deselectAll()
     */
    @SuppressWarnings("null")
    public void setSelection(int[] indices) {
        checkWidget();
        if (indices == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        deselectAll();
        int length = indices.length;
        if (length == 0 || ((getStyle() & SWT.SINGLE) != 0 && length > 1))
            return;
        int[] ids = new int[length];
        int count = 0;
        for (int i = 0; i < length; i++) {
            int index = indices[length - i - 1];
            if (index >= 0 && index < itemCount) {
                ids[count++] = index;
            }
        }
        if (count > 0) {
            select(ids);
            showIndex(ids[0] - 1);
        }
    }

    /**
     * Shows the item. If the item is already showing in the receiver, this
     * method simply returns. Otherwise, the items are scrolled until the item
     * is visible.
     * 
     * @param item the item to be shown
     * 
     * @exception IllegalArgumentException
     *                <ul>
     *                <li>ERROR_NULL_ARGUMENT - if the item is null</li>
     *                <li>ERROR_INVALID_ARGUMENT - if the item has been
     *                disposed</li>
     *                </ul>
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     * 
     * @see CompositeList#showSelection()
     */
    @SuppressWarnings("null")
    public void showItem(CompositeItem item) {
        checkWidget();
        if (item == null) {
            SWT.error(SWT.ERROR_NULL_ARGUMENT);
        }
        if (item.isDisposed()) {
            SWT.error(SWT.ERROR_INVALID_ARGUMENT);
        }
        int index = indexOf(item);
        if (index != -1) {
            showIndex(index);
        }
    }

    /**
     * Shows the selection. If the selection is already showing in the receiver,
     * this method simply returns. Otherwise, the items are scrolled until the
     * selection is visible.
     * 
     * @exception SWTException
     *                <ul>
     *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
     *                disposed</li>
     *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
     *                thread that created the receiver</li>
     *                </ul>
     * 
     * @see CompositeList#showItem(I)
     */
    public void showSelection() {
        checkWidget();
        if (selection.length > 0) {
            showIndex(selection[0]);
        }
    }

    private void deselect(int[] indexes) {
        int[] deselected = (indexes != null) ? indexes : selection;
        if (indexes == null) {
            selection = new int[4];
        } else {
            int[] newSelection = new int[selection.length - indexes.length];
            int i = 0;
            for (int j : selection) {
                if (Arrays.binarySearch(indexes, selection[j]) < 0) {
                    newSelection[i] = selection[j];
                }
            }
            selection = newSelection;
        }
        for (int i : deselected) {
            if (items[i] != null) {
                items[i].setSelected(false);
            }
        }
    }

    private void fixSelection(int index, boolean add) {
        int[] selection = getSelectionIndices();
        if (selection.length == 0)
            return;
        int newCount = 0;
        boolean fix = false;
        for (int i = 0; i < selection.length; i++) {
            if (!add && selection[i] == index) {
                fix = true;
            } else {
                int newIndex = newCount++;
                selection[newIndex] = selection[i] + 1;
                if (selection[newIndex] - 1 >= index) {
                    selection[newIndex] += add ? 1 : -1;
                    fix = true;
                }
            }
        }
        if (fix) {
            select(selection);
        }
    }

    private void select(int[] ids) {
        deselect(null);
        if (ids != null) {
            selection = ids;
        }
        for (int i : selection) {
            items[i].setSelected(true);
        }
    }

    protected void selectPrevious() {
        int index = selection[0];
        if (index == 0 || !items[index].isSelected()) {
            select(new int[] { itemCount - 1 });
        } else {
            select(new int[] { index - 1 });
        }
        showSelection();
    }

    protected void selectNext() {
        int index = selection[0];
        if (index == itemCount - 1 || !items[index].isSelected()) {
            select(new int[] { 0 });
        } else {
            select(new int[] { index + 1 });
        }
        showSelection();
    }

    private void showIndex(int index) {
        if (index < 0 || index >= itemCount) {
            return;
        }
        if (getParent() instanceof ScrolledComposite) {
            ScrolledComposite sc = (ScrolledComposite) getParent();
            Rectangle bounds = items[index].getBounds();
            Rectangle area = sc.getClientArea();
            Point origin = sc.getOrigin();
            if (origin.x > bounds.x) {
                origin.x = Math.max(0, bounds.x);
            }
            if (origin.y > bounds.y) {
                origin.y = Math.max(0, bounds.y);
            }
            if (origin.x + area.width < bounds.x + bounds.width) {
                origin.x = Math.max(0, bounds.x + bounds.width - area.width);
            }
            if (origin.y + area.height < bounds.y + bounds.height) {
                origin.y = Math.max(0, bounds.y + bounds.height - area.height);
            }
            sc.setOrigin(origin);
        }
    }

    protected void hookListeners() {

        addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent event) {
                switch (event.detail) {
                case SWT.TRAVERSE_ARROW_NEXT:
                    selectNext();
                    break;
                case SWT.TRAVERSE_ARROW_PREVIOUS:
                    selectPrevious();
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

    void itemCreated(I item, int index) {
        if (!(0 <= index && index <= itemCount)) {
            SWT.error(SWT.ERROR_INVALID_RANGE);
        }
        if (index != itemCount) {
            fixSelection(index, true);
        }
        if (itemCount == items.length) {
            I[] newItems = (I[]) new CompositeItem[itemCount + 4];
            System.arraycopy(items, 0, newItems, 0, items.length);
            items = newItems;
        }
        System.arraycopy(items, index, items, index + 1, itemCount++ - index);
        items[index] = item;
    }
}
