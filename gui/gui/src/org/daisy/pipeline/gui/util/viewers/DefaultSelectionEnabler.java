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

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * A default implementation of <code>ISelectionEnabler</code> based on
 * {@link ISelectionEnabler.Mode} and class types.
 * 
 * @author Romain Deltour
 * 
 */
public class DefaultSelectionEnabler implements ISelectionEnabler {

	/**
	 * Creates a new instance of this selection enabler for the given mode and
	 * set of classes.
	 * 
	 * @param mode
	 *            The mode of this selection enabler
	 * @param classes
	 *            The set of class items in the selection must inherit.
	 */
	public DefaultSelectionEnabler(Mode mode, Class<?>[] classes) {
		super();
		this.mode = mode;
		this.classes = classes;
	}

	public final boolean isEnabledFor(ISelection selection) {
		// Handle undefined selections.
		if (selection == null) {
			selection = StructuredSelection.EMPTY;
		}
		if (selection instanceof IStructuredSelection) {
			return isEnabledFor((IStructuredSelection) selection);
		}
		return false;
	}

	private Mode mode;
	private Class<?>[] classes;

	private boolean isClassCompatible(Object obj) {
		if (classes.length == 0) {
			return true;
		}
		for (Class<?> clazz : classes) {
			if (clazz.isInstance(obj)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Performs additional checks on the given selection. This default
	 * implementation returns always <code>true</code>.
	 * 
	 * @param selection
	 *            the selection received in {@link #isEnabledFor(ISelection)}
	 * @return <code>true</code>
	 */
	protected boolean checkContent(IStructuredSelection selection) {
		return true;
	}

	private boolean isEnabledFor(IStructuredSelection selection) {
		// Check size
		if (!mode.isSizeCompatible(selection.size())) {
			return false;
		}
		// Check class requirements
		if (classes.length != 0) {
			Iterator<?> iter = selection.iterator();
			while (iter.hasNext()) {
				if (!isClassCompatible(iter.next())) {
					return false;
				}
			}
		}
		// Check the selection content
		return checkContent(selection);
	}

}
