package org.daisy.pipeline.gui.jobs;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.pipeline.gui.jobs.model.JobManager;
import org.daisy.pipeline.gui.util.MutexRule;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * @author Romain Deltour
 * 
 */
public class SequentialJobsRunner extends AbstractJobsRunner {

    private static SequentialJobsRunner _default = new SequentialJobsRunner();

    private ISchedulingRule mutex;
    private Object family;
    private Job currentJob;
    private IJobChangeListener jobListener;
    private Comparator<JobRunnerJob> jobComparator;

    public SequentialJobsRunner() {
        mutex = new MutexRule();
        family = new Object();
        jobListener = new JobChangeAdapter() {
            @Override
            public void running(IJobChangeEvent event) {
                currentJob = event.getJob();
            }

            @Override
            public void done(IJobChangeEvent event) {
                currentJob = null;
            }
        };
        jobComparator = new Comparator<JobRunnerJob>() {
            private Comparator<JobInfo> comp = JobManager.getDefault()
                    .createComparator();

            public int compare(JobRunnerJob o1, JobRunnerJob o2) {
                return comp.compare(o1.getJobInfo(), o2.getJobInfo());
            }
        };
    }

    public static SequentialJobsRunner getDefault() {
        return _default;
    }

    @Override
    protected void doRun(List<JobInfo> jobInfos) {
        SortedSet<JobRunnerJob> runners = getRemainingRunners();
        for (JobInfo jobInfo : jobInfos) {
            JobRunnerJob runner = new JobRunnerJob(jobInfo, family);
            runner.addJobChangeListener(jobListener);
            runner.setRule(mutex);
            runners.add(runner);
            jobInfo.setRunnerJob(runner);
        }
        StateManager.getDefault().scheduled(jobInfos);
        for (JobRunnerJob job : runners) {
            job.schedule();
        }
    }

    protected SortedSet<JobRunnerJob> getRemainingRunners() {
        SortedSet<JobRunnerJob> runners = new TreeSet<JobRunnerJob>(
                jobComparator);
        Job.getJobManager().sleep(family);
        for (Job job : Job.getJobManager().find(family)) {
            if (job != currentJob) {
                job.cancel();
                runners.add((JobRunnerJob) job);
            }
        }
        return runners;
    }

}
