package org.daisy.pipeline.gui.messages;

import java.util.LinkedList;
import java.util.List;

import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.pipeline.gui.jobs.model.JobManager;
import org.daisy.pipeline.gui.util.CategorySet;
import org.daisy.pipeline.gui.util.Category;

/**
 * @author Romain Deltour
 * 
 */
class JobCategorySet extends CategorySet {

    public JobCategorySet() {
        super("Job");
    }

    @Override
    public List<Category> getCategories() {
        JobInfo[] jobs = JobManager.getDefault().toArray();
        List<Category> categories = new LinkedList<Category>();
        categories.add(new CoreCategory());
        for (JobInfo job : jobs) {
            categories.add(new JobCategory(job));
        }
        return categories;
    }

    class JobCategory extends Category {
        private JobInfo jobInfo;

        public JobCategory(JobInfo jobInfo) {
            super(jobInfo.getName());
            this.jobInfo = jobInfo;
        }

        @Override
        public boolean contains(Object obj) {
            if (obj instanceof JobMessageEvent) {
                JobMessageEvent message = (JobMessageEvent) obj;
                return jobInfo.equals(message.getJobInfo());
            }
            return false;
        }
    }

    class CoreCategory extends Category {

        public CoreCategory() {
            super("Core");
        }

        @Override
        public boolean contains(Object obj) {
            return !(obj instanceof JobMessageEvent);
        }

    }
}
