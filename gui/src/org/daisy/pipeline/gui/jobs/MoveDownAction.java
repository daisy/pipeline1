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

import org.daisy.pipeline.gui.IIconsKeys;
import org.daisy.pipeline.gui.GuiPlugin;
import org.eclipse.core.commands.operations.IUndoableOperation;

public class MoveDownAction extends MoveAction {

    public MoveDownAction(JobsView view) {
        super(view, Messages.action_moveDown, GuiPlugin.createDescriptor(IIconsKeys.MOVE_DOWN));
    }

    @Override
    public void propertyChanged(Object source, int propId) {
        if (propId == JobsView.PROP_SEL_JOB_INDEX) {
            setEnabled(jobManager.indexOf(selectedElem) < jobManager.size() - 1);
        }
    }

    @Override
    protected IUndoableOperation getOperation() {
        int index = jobManager.indexOf(selectedElem);
        return new MoveOperation(index, index + 1, selection);
    }

}