package org.daisy.pipeline.gui.parameters;

import org.daisy.pipeline.gui.model.JobInfo;
import org.daisy.pipeline.gui.util.viewers.CategorizedContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Romain Deltour
 * 
 */
public class ParamsContentProvider extends CategorizedContentProvider {

    private JobInfo job;

    @Override
    protected Object[] getAllElements() {
        return job.getJob().getJobParameters().values().toArray();
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        job = (JobInfo) newInput;
    }

}
