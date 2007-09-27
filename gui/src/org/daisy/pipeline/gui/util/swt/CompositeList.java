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
package org.daisy.pipeline.gui.util.swt;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TypedListener;

/**
 * A custom SWT widget for displaying a vertical list of custom
 * {@link CompositeItem}.
 * 
 * <p>
 * The API of this custom list has been inspired by the {@link List} and
 * {@link Table} widgets.
 * </p>
 * 
 * @author Romain Deltour
 * 
 * @param <I>
 *            A subclass of {@link CompositeItem} used for the list elements
 */
@SuppressWarnings("unchecked")
public class CompositeList<I extends CompositeItem> extends ScrolledComposite {
	/** The internal content area */
	Composite content;
	/** The number of items represented in this list */
	private int itemCount = 0;
	/** The indexes of the currently selected items */
	private int[] selection = new int[4];
	/** The array storing the items represented in this list */
	private I[] items = (I[]) new CompositeItem[4];

	/**
	 * Creates a new composite list widget with the given style flags and adds
	 * it as a child of the given composite.
	 * 
	 * @param parent
	 *            The composite to which the new list will be attached
	 * @param style
	 *            The SWT style flags to apply to this list
	 */
	public CompositeList(Composite parent, int style) {
		super(parent, style);
		content = new Composite(this, SWT.NONE);
		this.setContent(content);
		this.setExpandHorizontal(true);
		this.setExpandVertical(true);
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		layout.verticalSpacing = 0;
		content.setLayout(layout);
		content.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_LIST_BACKGROUND));
		hookListeners();
		initAccessible();
	}

	/**
	 * Adds the listener to the collection of listeners who will be notified
	 * when the user changes the receiver's selection, by sending it one of the
	 * messages defined in the <code>SelectionListener</code> interface.
	 * <p>
	 * <code>widgetSelected</code> is called when the user changes the
	 * selected tab. <code>widgetDefaultSelected</code> is not called.
	 * </p>
	 * 
	 * @param listener
	 *            the listener which should be notified when the user changes
	 *            the receiver's selection
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SelectionListener
	 * @see #removeSelectionListener
	 * @see SelectionEvent
	 */
	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		TypedListener typedListener = new TypedListener(listener);
		addListener(SWT.Selection, typedListener);
		addListener(SWT.DefaultSelection, typedListener);
	}

	/**
	 * Deselect the items at the given indexes.
	 * 
	 * @param indexes
	 *            The indexes of the item to be deselected
	 */
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
	 * Update the selection array after the addition or removal of an item.
	 * 
	 * @param index
	 *            the index of the added or removed item
	 * @param add
	 *            whether the change was an addition or a removal
	 */
	private void fixSelection(int index, boolean add) {
		int[] selection = getSelectionIndices();
		if (selection.length == 0) {
			return;
		}
		int newCount = 0;
		boolean fix = false;
		for (int i = 0; i < selection.length; i++) {
			if (!add && (selection[i] == index)) {
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

	@Override
	public boolean forceFocus() {
		checkWidget();
		if (itemCount > 0) {
			if ((selection == null) || (selection.length == 0)) {
				select(new int[] { 0 });
			}
			if (items[selection[0]].setFocus()) {
				return true;
			}
		}
		return super.forceFocus();
	}

	/**
	 * Returns the item at the given, zero-relative index in the receiver.
	 * Throws an exception if the index is out of range.
	 * 
	 * @param index
	 *            the index of the item to return
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
		if (!((0 <= index) && (index < itemCount))) {
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
	 * Called right after the creation of the list to be able to hook the SWT
	 * listener to this widget.
	 * <p>
	 * This implementation adds a {@link TraverseListener} to enable selecting
	 * the previous or next item with the arrow keys
	 * </p>
	 */
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

		addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.widget != CompositeList.this) {
					return;
				}
				if ((e.item != null) && (e.item instanceof CompositeItem)) {
					((CompositeItem) e.item).setFocus();
				}
			}

		});
	}

	/**
	 * Searches the receiver's list starting at the first item (index 0) until
	 * an item is found that is equal to the argument, and returns the index of
	 * that item. If no item is found, returns -1.
	 * 
	 * @param item
	 *            the search item
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
	 * Initializes the accessible object of this custom control
	 */
	private void initAccessible() {
		final Accessible accessible = getAccessible();
		accessible.addAccessibleControlListener(new AccessibleControlAdapter() {

			@Override
			public void getChild(AccessibleControlEvent e) {
				if (e.childID == ACC.CHILDID_SELF) {
					e.accessible = accessible;
				} else if ((e.childID >= 0) && (e.childID < itemCount)) {
					e.accessible = items[e.childID].getAccessible();
				}
			}

			@Override
			public void getChildAtPoint(AccessibleControlEvent e) {

				Point testPoint = content.toControl(e.x, e.y);
				for (int i = 0; i < itemCount; i++) {
					if (items[i].getBounds().contains(testPoint)) {
						e.accessible = items[i].getAccessible();
						return;
					}
				}
				Rectangle location = getBounds();
				location.height = location.height - getClientArea().height;
				if (location.contains(testPoint)) {
					e.childID = ACC.CHILDID_SELF;
				} else {
					e.childID = ACC.CHILDID_NONE;
				}
			}

			@Override
			public void getChildCount(AccessibleControlEvent e) {
				e.detail = itemCount;
			}

			@Override
			public void getChildren(AccessibleControlEvent e) {
				Object[] children = new Object[itemCount];
				for (int i = 0; i < itemCount; i++) {
					children[i] = items[i].getAccessible();
				}
				e.children = children;
			}

			@Override
			public void getFocus(AccessibleControlEvent e) {
				for (int i = 0; i < itemCount; i++) {
					if (items[i].isFocusControl()) {
						e.accessible = items[i].getAccessible();
						return;
					}
				}
				if (isFocusControl()) {
					e.childID = ACC.CHILDID_SELF;
				} else {
					e.childID = ACC.CHILDID_NONE;
				}
			}

			@Override
			public void getLocation(AccessibleControlEvent e) {
				Rectangle location = null;
				int childID = e.childID;
				if (childID == ACC.CHILDID_SELF) {
					location = getBounds();
				} else if ((childID >= 0) && (childID < items.length)) {
					location = items[childID].getBounds();
				}
				if (location != null) {
					Point pt = toDisplay(location.x, location.y);
					e.x = pt.x;
					e.y = pt.y;
					e.width = location.width;
					e.height = location.height;
				}

			}

			@Override
			public void getRole(AccessibleControlEvent e) {
				int role = 0;
				int childID = e.childID;
				if (childID == ACC.CHILDID_SELF) {
					role = ACC.ROLE_LIST;
				} else if ((childID >= 0) && (childID < itemCount)) {
					role = ACC.ROLE_LISTITEM;
				}
				e.detail = role;
			}

			@Override
			public void getSelection(AccessibleControlEvent e) {
				if (selection.length > 0) {
					e.accessible = items[selection[0]].getAccessible();
				} else {
					e.childID = ACC.CHILDID_NONE;
				}
			}

			@Override
			public void getState(AccessibleControlEvent e) {

				int state = 0;
				int childID = e.childID;
				if (childID == ACC.CHILDID_SELF) {
					state = (isFocusControl()) ? ACC.STATE_FOCUSED
							: ACC.STATE_NORMAL;
				} else if ((childID >= 0) && (childID < itemCount)) {
					state = ACC.STATE_SELECTABLE;
					if (isFocusControl()) {
						state |= ACC.STATE_FOCUSABLE;
					}
					if (items[childID].isSelected()) {
						state |= ACC.STATE_SELECTED;
					}
					if (items[childID].isFocusControl()) {
						state |= ACC.STATE_FOCUSED;
					}
				}
				e.detail = state;
			}
		});

		addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (CompositeList.this == event.widget) {
					accessible.selectionChanged();
				}
			}
		});

		addListener(SWT.FocusIn, new Listener() {
			public void handleEvent(Event event) {
				accessible.setFocus(ACC.CHILDID_SELF);
			}
		});
	}

	/**
	 * Must be called right after the creation of a {@link CompositeItem} that
	 * was added to this list, to update its data model.
	 * 
	 * @param item
	 *            The newly created item
	 * @param index
	 *            The index of the newly created item
	 */
	void itemCreated(I item, int index) {
		if (!((0 <= index) && (index <= itemCount))) {
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
		itemsChanged();
	}

	private void itemsChanged() {
		setMinSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Removes the item from the receiver at the given zero-relative index.
	 * 
	 * @param index
	 *            the index for the item
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
		if (!((0 <= index) && (index < itemCount))) {
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
		itemsChanged();
	}

	/**
	 * Removes the items from the receiver which are between the given
	 * zero-relative start and end indices (inclusive).
	 * 
	 * @param start
	 *            the start of the range
	 * @param end
	 *            the end of the range
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
		if (start > end) {
			return;
		}
		if (!((0 <= start) && (start <= end) && (end < itemCount))) {
			SWT.error(SWT.ERROR_INVALID_RANGE);
		}
		if ((start == 0) && (end == itemCount - 1)) {
			removeAll();
		} else {
			int length = end - start + 1;
			for (int i = 0; i < length; i++) {
				remove(start);
			}
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
			if ((item != null) && !item.isDisposed()) {
				item.dispose();
			}
		}
		itemCount = 0;
		items = (I[]) new CompositeItem[4];
		selection = new int[4];
		itemsChanged();
	}

	/**
	 * Removes the listener from the collection of listeners who will be
	 * notified when the user changes the receiver's selection.
	 * 
	 * @param listener
	 *            the listener which should no longer be notified
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 * 
	 * @see SelectionListener
	 * @see #addSelectionListener
	 */
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		removeListener(SWT.Selection, listener);
		removeListener(SWT.DefaultSelection, listener);
	}

	/**
	 * Select the items at the given indexes.
	 * 
	 * @param ids
	 *            An array of indexes
	 */
	private void select(int[] ids) {
		deselect(null);
		if (ids != null) {
			selection = ids;
		}
		for (int i : selection) {
			items[i].setSelected(true);
		}
	}

	/**
	 * Selects the item just after the current selection.
	 * <p>
	 * Does nothing if the currently selected item is the last item in the list.<p/>
	 * <p>
	 * If no item is currently selected, selects the first item in the list.
	 * </p>
	 */
	void selectNext() {
		int index = selection[0];
		if (!items[index].isSelected()) {
			setSelection(new int[] { 0 }, true);
		} else if ((index != itemCount - 1)) {
			setSelection(new int[] { index + 1 }, true);
		}
		showSelection();
	}

	/**
	 * Selects the item just before the current selection.
	 * <p>
	 * Does nothing if the currently selected item is the first item in the
	 * list.<p/>
	 * <p>
	 * If no item is currently selected, selects the last item in the list.
	 * </p>
	 */
	void selectPrevious() {
		int index = selection[0];
		if (!items[index].isSelected()) {
			setSelection(new int[] { itemCount - 1 }, true);
		} else if (index != 0) {
			setSelection(new int[] { index - 1 }, true);
		}
		showSelection();
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
	 * @param indices
	 *            the indices of the items to select
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
		if ((length == 0) || (((getStyle() & SWT.SINGLE) != 0) && (length > 1))) {
			return;
		}
		int[] ids = new int[length];
		int count = 0;
		for (int i = 0; i < length; i++) {
			int index = indices[length - i - 1];
			if ((index >= 0) && (index < itemCount)) {
				ids[count++] = index;
			}
		}
		if (count > 0) {
			select(ids);
			showIndex(ids[0] - 1);
		}
	}

	void setSelection(int[] indices, boolean notify) {
		int[] oldIndices = selection;
		setSelection(indices);
		if (notify && (!Arrays.equals(oldIndices, indices))
				&& (indices.length != 0)) {
			Event event = new Event();
			event.item = getItem(indices[0]);
			notifyListeners(SWT.Selection, event);
		}
	}

	/**
	 * Makes sure the item at <code>index</code> is visible.
	 * 
	 * @param index
	 *            An index in the list
	 */
	private void showIndex(int index) {
		if ((index < 0) || (index >= itemCount)) {
			return;
		}
		Rectangle bounds = items[index].getBounds();
		Rectangle area = getClientArea();
		Point origin = getOrigin();
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
		setOrigin(origin);
	}

	/**
	 * Shows the item. If the item is already showing in the receiver, this
	 * method simply returns. Otherwise, the items are scrolled until the item
	 * is visible.
	 * 
	 * @param item
	 *            the item to be shown
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
	 * @see CompositeList#showItem(CompositeItem)
	 */
	public void showSelection() {
		checkWidget();
		if (selection.length > 0) {
			showIndex(selection[0]);
		}
	}
}
