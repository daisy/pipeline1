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
package org.daisy.pipeline.gui.jobs;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.daisy.pipeline.gui.model.IJobsFilter;
import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.model.JobStateFilter;
import org.daisy.pipeline.gui.model.StateManager;
import org.daisy.pipeline.gui.util.actions.AbstractActionDelegate;
import org.daisy.util.execution.State;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * The Action used to cancel a running Pipeline job.
 * 
 * @author Romain Deltour
 * 
 */
public class CancelAction extends AbstractActionDelegate {
	IStructuredSelection selection;
	private IJobsFilter stateFilter = new JobStateFilter(EnumSet.of(
			State.RUNNING, State.WAITING));

	@Override
	public void run(IAction action) {
		if (selection == null) {
			return;
		}
		// Extract the selected jobInfos
		List<JobInfo> jobInfos = new LinkedList<JobInfo>();
		Iterator<?> iter = selection.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (element instanceof JobInfo) {
				jobInfos.add((JobInfo) element);
			}
		}
		StateManager.getDefault().cancel(stateFilter.filter(jobInfos));
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
}
