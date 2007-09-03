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
package org.daisy.pipeline.gui.progress;

import org.daisy.pipeline.gui.util.swt.CompositeList;
import org.daisy.pipeline.gui.util.viewers.CompositeListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * The JFace viewer used to display the list of tasks of a Pipeline Job.
 * <p>
 * This viewer uses a {@link CompositeListViewer} of {@link TaskItem}s as its
 * internal widget.
 * </p>
 * 
 * @author Romain Deltour
 * 
 */
public class TaskListViewer extends CompositeListViewer<TaskItem> {
	/**
	 * Creates a task list viewer on a newly-created composite list control
	 * under the given parent. The list control is created using the SWT style
	 * bits <code>MULTI, H_SCROLL, V_SCROLL,</code> and <code>BORDER</code>.
	 * The viewer has no input, no content provider, a default label provider,
	 * no sorter, and no filters.
	 * 
	 * @param parent
	 *            the parent control
	 */
	public TaskListViewer(Composite parent) {
		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
	}

	/**
	 * Creates a task list viewer on a newly-created composite list control
	 * under the given parent. The list control is created using the given style
	 * bits. The viewer has no input, no content provider, a default label
	 * provider, no sorter, and no filters.
	 * 
	 * @param parent
	 *            the parent control
	 * @param style
	 *            SWT style bits
	 */
	public TaskListViewer(Composite parent, int style) {
		this(new CompositeList<TaskItem>(parent, style));
	}

	/**
	 * Creates a task list viewer on the given composite list control. The
	 * viewer has no input, no content provider, a default label provider, no
	 * sorter, and no filters.
	 * 
	 * @param list
	 *            the parent list
	 */
	public TaskListViewer(CompositeList<TaskItem> list) {
		super(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.daisy.pipeline.gui.jobdetails.CompositeListViewer#createItem(org.daisy.pipeline.gui.jobdetails.CompositeList,
	 *      int)
	 */
	@Override
	protected TaskItem createItem(CompositeList<TaskItem> parent, int index) {
		return new TaskItem(parent, SWT.NONE, index);
	}

}
