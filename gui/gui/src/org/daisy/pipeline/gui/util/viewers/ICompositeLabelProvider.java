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
package org.daisy.pipeline.gui.util.viewers;

import java.util.Set;

import org.daisy.pipeline.gui.util.swt.CompositeItem;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider to use with {@link CompositeListViewer}s.
 * <p>
 * A label provider maps an element of the viewer's model to optional images,
 * optional integer values, and optional text strings used to display the
 * element in the viewer's control.
 * </p>
 * <p>
 * A label provider must not be shared between viewers since a label provider
 * generally manages SWT resources (images), which must be disposed when the
 * viewer is disposed. To simplify life cycle management, the current label
 * provider of a viewer is disposed when the viewer is disposed.
 * </p>
 * 
 * @see IBaseLabelProvider
 * @see ILabelProvider
 * @see ITableLabelProvider
 * 
 * @author Romain Deltour
 */
public interface ICompositeLabelProvider extends IBaseLabelProvider {

	/**
	 * Returns the image for the label of the given element associated to the
	 * given key. The image is owned by the label provider and must not be
	 * disposed directly. Instead, dispose the label provider when no longer
	 * needed.
	 * 
	 * @param key
	 *            they key of a label
	 * @param element
	 *            the element for which to provide the label image
	 * @return the image used to label the element, or <code>null</code> if
	 *         there is no image for the given object
	 */
	public Image getImage(String key, Object element);

	/**
	 * Returns the set of keys representing the image labels of a
	 * {@link CompositeItem} subclass this label provider is used with.
	 * 
	 * @return The set of keys representing the different image labels
	 */
	public Set<String> getImageKeys();

	/**
	 * Returns the integer value for the label of the given element associated
	 * to the given key.
	 * 
	 * @param key
	 *            they key of a label
	 * @param element
	 *            the element for which to provide the label value
	 * @return the integer value used to label the element, or <code>null</code>
	 *         if there is no value for the given object
	 */
	public int getInt(String key, Object element);

	/**
	 * Returns the set of keys representing the integer labels of a
	 * {@link CompositeItem} subclass this label provider is used with.
	 * 
	 * @return The set of keys representing the different integer labels
	 */
	public Set<String> getIntKeys();

	/**
	 * Returns the text for the label of the given element associated to the
	 * given key.
	 * 
	 * @param key
	 *            they key of a label
	 * @param element
	 *            the element for which to provide the label text
	 * @return the text used to label the element, or <code>null</code> if
	 *         there is no text for the given object
	 */
	public String getText(String key, Object element);

	/**
	 * Returns the set of keys representing the text labels of a
	 * {@link CompositeItem} subclass this label provider is used with.
	 * 
	 * @return The set of keys representing the different text labels
	 */
	public Set<String> getTextKeys();
}
