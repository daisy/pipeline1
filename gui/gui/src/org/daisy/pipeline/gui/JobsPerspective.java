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
package org.daisy.pipeline.gui;

import org.daisy.pipeline.gui.jobs.JobsView;
import org.daisy.pipeline.gui.messages.MessagesView;
import org.daisy.pipeline.gui.progress.JobProgressView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class JobsPerspective implements IPerspectiveFactory {

    public static final String ID = "org.daisy.pipeline.gui.perspectives.jobs"; //$NON-NLS-1$
    public static final String DETAILS_FODER_ID = ID + "detailsFolder"; //$NON-NLS-1$

    public void createInitialLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        layout.setEditorAreaVisible(false);
        layout.addView(JobsView.ID, IPageLayout.LEFT, 1.0f, editorArea);
        layout.addView(MessagesView.ID, IPageLayout.BOTTOM, 0.7f, JobsView.ID);
        IFolderLayout folder = layout.createFolder(DETAILS_FODER_ID,
                IPageLayout.RIGHT, 0.5f, JobsView.ID);
        // folder.addView(ParametersView.ID);
        folder.addView(JobProgressView.ID);

        PerspectiveUtil.addCommonShortcuts(layout);
    }

}
