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

import org.eclipse.jface.viewers.ISelection;

/**
 * Interfaced to be used by an Action to specify whether it is enabled for a
 * given selection.
 * 
 * @author Romain Deltour
 * 
 */
public interface ISelectionEnabler {
	/**
	 * An enumeration of possible modes for a selection enabler.
	 * 
	 */
	public enum Mode {
		/** Enabled for any selection size */
		ANY_NUMBER,
		/** Enabled when the selection is empty */
		NONE,
		/** Enabled when the selection is empty or has one element */
		NONE_OR_ONE,
		/** Enabled when the selection has one or more elements */
		ONE_OR_MORE,
		/** Enabled when the selection has exactly one element */
		SINGLE,
		/** Enabled when the selection has more than one element */
		MULTIPLE;

		/**
		 * Whether the given selection size is compatible with this Mode.
		 * 
		 * @param size
		 *            the size of a selection
		 * @return <code>true</code> if and only if <code>size</code> is
		 *         compatible with this mode.
		 */
		public boolean isSizeCompatible(int size) {
			switch (this) {
			case ANY_NUMBER:
				return true;
			case NONE:
				return size == 0;
			case NONE_OR_ONE:
				return size <= 1;
			case ONE_OR_MORE:
				return size != 0;
			case SINGLE:
				return size == 1;
			case MULTIPLE:
				return size > 2;
			default:
				return false;
			}
		}
	}

	/**
	 * Whether this enabler allows the given selection.
	 * 
	 * @param selection
	 *            a selection
	 * @return <code>true</code> if and only if the object using this is
	 *         enabled for the given selection
	 */
	public boolean isEnabledFor(ISelection selection);
}
