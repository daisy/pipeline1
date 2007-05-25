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

import org.daisy.pipeline.gui.doc.DocView;
import org.daisy.pipeline.gui.jobs.JobsView;
import org.daisy.pipeline.gui.jobs.wizard.NewJobWizard;
import org.daisy.pipeline.gui.messages.MessagesView;
import org.daisy.pipeline.gui.progress.JobProgressView;
import org.eclipse.ui.IPageLayout;

/**
 * @author Romain Deltour
 * 
 */
public final class PerspectiveUtil {
    private PerspectiveUtil() {
    }

    public static void addCommonShortcuts(IPageLayout layout) {
        // Perspective shortcuts
        layout.addPerspectiveShortcut(JobsPerspective.ID);
        layout.addPerspectiveShortcut(DocPerspective.ID);
        // View shortcuts
        layout.addShowViewShortcut(DocView.ID);
        layout.addShowViewShortcut(JobsView.ID);
        layout.addShowViewShortcut(MessagesView.ID);
        // layout.addShowViewShortcut(ParametersView.ID);
        layout.addShowViewShortcut(JobProgressView.ID);
        layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView"); //$NON-NLS-1$
        // Wizard shortcuts
        layout.addNewWizardShortcut(NewJobWizard.ID);
    }
}
