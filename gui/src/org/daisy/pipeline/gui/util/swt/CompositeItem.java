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

import org.daisy.pipeline.gui.util.viewers.CompositeListViewer;
import org.daisy.pipeline.gui.util.viewers.ICompositeLabelProvider;
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

/**
 * The abstract superclass of composite widgets representing items of
 * {@link CompositeList}s.
 * 
 * @see CompositeList
 * @author Romain Deltour
 * 
 */
@SuppressWarnings("unchecked")
public abstract class CompositeItem extends Composite {

	/**
	 * Checks that the given list is not <code>null</code> and raises an
	 * {@link SWT#ERROR_NULL_ARGUMENT} otherwise.
	 * 
	 * @param control
	 *            A composite list.
	 * @return <code>control</code> if it is not <code>null</code>
	 */
	private static CompositeList<?> checkNull(CompositeList<?> control) {
		if (control == null) {
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		}
		return control;
	}

	/** The list containing this item */
	private CompositeList parentList;
	/** Whether this item is selected */
	private boolean selected;

	/**
	 * Creates a new item with the given style flag and appends it to the given
	 * list.
	 * <p>
	 * Note that subclasses are responsible for creating themselves on
	 * type-compatible lists.
	 * </p>
	 * 
	 * @param parent
	 *            the parent list of the created item
	 * @param style
	 *            the SWT style flags
	 */
	public CompositeItem(CompositeList parent, int style) {
		this(parent, style, checkNull(parent).getItemCount());
	}

	// subclasses are responsible for creating themselves on compatible lists
	/**
	 * Creates a new item with the given style flag and adds it to the given
	 * list at the given index.
	 * <p>
	 * Note that subclasses are responsible for creating themselves on
	 * type-compatible lists.
	 * </p>
	 * 
	 * @param parent
	 *            the parent list of the created item
	 * @param style
	 *            the SWT style flags
	 * @param index
	 *            the index at which the item will be inserted
	 * 
	 */
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

	/**
	 * Creates the content of this composite item
	 */
	protected abstract void createChildren();

	/**
	 * Called at the end of the constructor to add SWT listeners to the item.
	 * The default implementation adds a mouse listener for selecting this item
	 * on click events and adds a traversal listener for selecting previous/next
	 * items with arrow left/right.
	 */
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
					break;
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

	/**
	 * Whether this item is selected.
	 * 
	 * @return <code>true</code> if and only if this item is selected
	 */
	boolean isSelected() {
		return selected;
	}

	/**
	 * Refreshes the content of this item. The default implementation merely
	 * returns when this item is disposed.
	 */
	public void refresh() {
		if (isDisposed()) {
			return;
		}
	}

	/**
	 * Refreshes the color used by this item and its children according to the
	 * selection status.
	 */
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

	/**
	 * Sets the image corresponding to the given key to the given image. This
	 * method must be overridden by subclasses when a JFace-like
	 * {@link ICompositeLabelProvider} and {@link CompositeListViewer} are used
	 * to update the parent {@link CompositeList}
	 * 
	 * @param key
	 *            the key of the image to set
	 * @param image
	 *            the new image
	 * @see ICompositeLabelProvider
	 * @see CompositeListViewer
	 */
	public void setImage(String key, Image image) {
	}

	/**
	 * Sets the integer value corresponding to the given key to the given image.
	 * This method must be overridden by subclasses when a JFace-like
	 * {@link ICompositeLabelProvider} and {@link CompositeListViewer} are used
	 * to update the parent {@link CompositeList}
	 * 
	 * @param key
	 *            the key of the integer value to set
	 * @param value
	 *            the new integer value
	 * @see ICompositeLabelProvider
	 * @see CompositeListViewer
	 */
	public void setInt(String key, int value) {
	}

	/**
	 * Set the selection state of this item.
	 * 
	 * @param selected
	 *            whether this item should be selected
	 */
	void setSelected(boolean selected) {
		this.selected = selected;
		refreshColors();
	}

	/**
	 * Sets the text corresponding to the given key to the given text. This
	 * method must be overridden by subclasses when a JFace-like
	 * {@link ICompositeLabelProvider} and {@link CompositeListViewer} are used
	 * to update the parent {@link CompositeList}
	 * 
	 * @param key
	 *            the key of the text to set
	 * @param text
	 *            the new text
	 * @see ICompositeLabelProvider
	 * @see CompositeListViewer
	 */
	public void setText(String key, String text) {
	}

}
