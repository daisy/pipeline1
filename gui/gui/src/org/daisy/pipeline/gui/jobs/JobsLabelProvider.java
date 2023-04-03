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

import org.daisy.pipeline.gui.GuiPlugin;
import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.model.JobInfo;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Provides the label for the jobs tree used in the Jobs view.
 * 
 * @author Romain Deltour
 * @author Laurie Sherve
 */
public class JobsLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if ((element instanceof JobInfo) && (columnIndex == 0)) {
			switch (((JobInfo) element).getSate()) {
			case ABORTED:
				return GuiPlugin.getImage(IIconsKeys.STATE_CANCELED);
			case FAILED:
				return GuiPlugin.getImage(IIconsKeys.STATE_FAILED);
			case FINISHED:
				return GuiPlugin.getImage(IIconsKeys.STATE_FINISHED);
			case IDLE:
				return GuiPlugin.getImage(IIconsKeys.STATE_IDLE);
			case RUNNING:
				return GuiPlugin.getImage(IIconsKeys.STATE_RUNNING);
			case WAITING:
				return GuiPlugin.getImage(IIconsKeys.STATE_WAITING);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider
	 */
	public String getColumnText(Object element, int columnIndex) {
		// WARNING: we do not return the labels of parameters as they are
		// custom-painted in the JobsView class
		if (element instanceof JobInfo) {
			JobInfo job = (JobInfo) element;
			switch (columnIndex) {
			case 0:
				return job.getName();
			case 1:
				return job.getSate().toString();
			}
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		return getColumnText(element, 0);
	}

}
