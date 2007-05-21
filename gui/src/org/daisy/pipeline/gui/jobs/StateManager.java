package org.daisy.pipeline.gui.jobs;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.daisy.dmfc.core.event.BusListener;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.JobStateChangeEvent;
import org.daisy.dmfc.core.event.ProgressChangeEvent;
import org.daisy.dmfc.core.event.StateChangeEvent;
import org.daisy.dmfc.core.event.TaskProgressChangeEvent;
import org.daisy.dmfc.core.event.TaskStateChangeEvent;
import org.daisy.dmfc.core.event.UserAbortEvent;
import org.daisy.dmfc.core.event.StateChangeEvent.Status;
import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Task;
import org.daisy.pipeline.gui.jobs.model.JobInfo;
import org.daisy.pipeline.gui.jobs.model.JobManager;
import org.daisy.pipeline.gui.tasks.TaskInfo;
import org.daisy.util.execution.State;

/**
 * @author Romain Deltour
 * 
 */
public class StateManager implements BusListener {
    private static StateManager _default = new StateManager();
    // TODO check null/thread safety
    // private Object lock = new Object();
    private Map<JobRunnerJob, JobInfo> runningJobs;
    private Map<JobRunnerJob, TaskInfo> runningTasks;
    private List<IJobChangeListener> jobListeners;
    private List<ITaskChangeListener> taskListeners;

    public StateManager() {
        runningJobs = new HashMap<JobRunnerJob, JobInfo>();
        runningTasks = new HashMap<JobRunnerJob, TaskInfo>();
        jobListeners = new ArrayList<IJobChangeListener>();
        taskListeners = new ArrayList<ITaskChangeListener>();
    }

    public static StateManager getDefault() {
        return _default;
    }

    public void init() {
        EventBus.getInstance().subscribe(this, ProgressChangeEvent.class);
        EventBus.getInstance().subscribe(this, StateChangeEvent.class);
    }

    public void dispose() {
        EventBus.getInstance().unsubscribe(this, ProgressChangeEvent.class);
        EventBus.getInstance().unsubscribe(this, StateChangeEvent.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.daisy.dmfc.core.event.BusListener#received(java.util.EventObject)
     */
    public void received(EventObject event) {
        if (event instanceof TaskProgressChangeEvent) {
            TaskProgressChangeEvent tpce = (TaskProgressChangeEvent) event;
            progressChanged((Task) tpce.getSource(), tpce.getProgress());
        }
        if (event instanceof TaskStateChangeEvent) {
            TaskStateChangeEvent tce = (TaskStateChangeEvent) event;
            stateChanged((Task) tce.getSource(), tce.getState());
        }
        if (event instanceof JobStateChangeEvent) {
            JobStateChangeEvent ssce = (JobStateChangeEvent) event;
            stateChanged((Job) ssce.getSource(), ssce.getState());
        }
    }

    /**
     * @param transformer
     * @param progress
     */
    protected void progressChanged(Task task, double progress) {
        // [Hack] We get the current job runner as a key to the running jobs
        // map,
        // since we can't acces the Pipeline job from the task
        JobRunnerJob runner = (JobRunnerJob) JobRunnerJob.getJobManager()
                .currentJob();
        TaskInfo runningTask = runningTasks.get(runner);
        if (runningTask != null) {
            runningTask.setProgress(progress);
            fireChanged(runningTask);
        }
    }

    /**
     * @param transformer
     * @param state
     */
    protected void stateChanged(Task task, Status status) {
        // [Hack] We get the current job runner as a key to the running jobs
        // map,
        // since we can't acces the Pipeline job from the task
        JobRunnerJob runner = (JobRunnerJob) JobRunnerJob.getJobManager()
                .currentJob();
        TaskInfo runningTask = null;
        switch (status) {
        case STARTED:
            JobInfo runningJob = runningJobs.get(runner);
            if (runningJob != null) {
                Iterator<TaskInfo> iter = runningJob.getTasks().iterator();
                while (iter.hasNext() && runningTask == null) {
                    TaskInfo info = iter.next();
                    if (info.getTask() == task) {
                        runningTask = info;
                    }
                }
            }
            if (runningTask != null) {
                runningTasks.put(runner, runningTask);
                runningTask.setState(State.RUNNING);
                fireChanged(runningTask);
            }
            break;
        case STOPPED:
            runningTask = runningTasks.remove(runner);
            if (runningTask != null) {
                runningTask.setProgress(1.0);
                runningTask.setState(State.FINISHED);
                fireChanged(runningTask);
            }
            break;
        default:
            break;
        }
    }

    protected void stateChanged(Job job, Status state) {
        JobInfo jobInfo = JobManager.getDefault().get(job);
        JobRunnerJob runner = jobInfo.getRunnerJob();
        if (runner == null) {
            throw new IllegalStateException("Can't find the runner job of "
                    + jobInfo.getName());
        }
        switch (state) {
        case STARTED:
            runningJobs.put(runner, jobInfo);
            jobInfo.setState(State.RUNNING);
            fireChanged(jobInfo);
            break;
        case STOPPED:
            runningJobs.remove(runner);
            jobInfo.setState(State.FINISHED);
            fireChanged(jobInfo);
            break;
        default:
            break;
        }
    }

    private void fireChanged(TaskInfo task) {
        for (ITaskChangeListener listener : taskListeners) {
            listener.taskChanged(task);
        }
    }

    private void fireChanged(JobInfo job) {
        for (IJobChangeListener listener : jobListeners) {
            listener.jobChanged(job);
        }
    }

    private void fireChanged(List<JobInfo> jobInfos) {
        for (IJobChangeListener listener : jobListeners) {
            listener.jobsChanged(jobInfos);
        }
    }

    public void aborted(JobInfo jobInfo) {
        JobRunnerJob runner = jobInfo.getRunnerJob();
        if (runner == null) {
            throw new IllegalStateException("Can't find the runner job of "
                    + jobInfo.getName());
        }
        // Set the job state
        runningJobs.remove(runner);
        jobInfo.setState(State.ABORTED);
        // Set the running task state
        TaskInfo runningTask = runningTasks.remove(runner);
        if (runningTask != null) {
            runningTask.setState(State.ABORTED);
            fireChanged(runningTask);
        }
        fireChanged(jobInfo);
    }

    public void cancel(List<JobInfo> jobInfos) {
        for (JobInfo jobInfo : jobInfos) {
            if (jobInfo.getSate() == State.RUNNING) {
                EventBus.getInstance().publish(new UserAbortEvent(this));
            } else {
                JobRunnerJob runner = jobInfo.getRunnerJob();
                if (runner == null) {
                    throw new IllegalStateException(
                            "Can't find the runner job of " + jobInfo.getName());
                }
                jobInfo.getRunnerJob().cancel();
                jobInfo.setState(State.IDLE);
            }
        }
        fireChanged(jobInfos);
    }

    public void failed(JobInfo jobInfo) {
        JobRunnerJob runner = jobInfo.getRunnerJob();
        if (runner == null) {
            throw new IllegalStateException("Can't find the runner job of "
                    + jobInfo.getName());
        }
        // Set the job state
        runningJobs.remove(runner);
        jobInfo.setState(State.FAILED);
        // Set the running task state
        TaskInfo runningTask = runningTasks.remove(runner);
        if (runningTask != null) {
            runningTask.setState(State.FAILED);
            fireChanged(runningTask);
        }
        fireChanged(jobInfo);
    }

    public void reset(List<JobInfo> jobInfos) {
        for (JobInfo jobInfo : jobInfos) {
            jobInfo.setState(State.IDLE);
        }
        fireChanged(jobInfos);
    }

    public void scheduled(List<JobInfo> jobInfos) {
        for (JobInfo jobInfo : jobInfos) {
            jobInfo.setState(State.WAITING);
        }
        fireChanged(jobInfos);
    }

    /**
     * @param provider
     */
    public void addTaskChangeListener(ITaskChangeListener listener) {
        if (!taskListeners.contains(listener)) {
            taskListeners.add(listener);
        }
    }

    /**
     * @param provider
     */
    public void removeTaskChangeListener(ITaskChangeListener listener) {
        taskListeners.remove(listener);
    }

    /**
     * @param view
     */
    public void addJobChangeListener(IJobChangeListener listener) {
        if (!jobListeners.contains(listener)) {
            jobListeners.add(listener);
        }

    }

    /**
     * @param view
     */
    public void removeJobChangeListener(IJobChangeListener listener) {
        jobListeners.remove(listener);
    }

}
