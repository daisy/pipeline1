package org.daisy.pipeline.gui.jobs;

import org.daisy.pipeline.gui.jobs.model.Queue;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class JobsContentProvider implements IStructuredContentProvider {

    public Object[] getElements(Object inputElement) {
        return ((Queue) inputElement).getLinkedListJobs().toArray();
    }

    public void dispose() {
        // Nothing to do here.
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // Nothing to do here.
    }

}
