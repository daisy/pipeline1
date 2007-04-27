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
    private Map<JobsRunner, JobInfo> runningJobs;
    private Map<JobsRunner, TaskInfo> runningTasks;
    private List<IJobChangeListener> jobListeners;
    private List<ITaskChangeListener> taskListeners;

    public StateManager() {
        runningJobs = new HashMap<JobsRunner, JobInfo>();
        runningTasks = new HashMap<JobsRunner, TaskInfo>();
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
    private void progressChanged(Task task, double progress) {
        JobsRunner runner = (JobsRunner) JobsRunner.getJobManager()
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
    private void stateChanged(Task task, Status status) {
        JobsRunner runner = (JobsRunner) JobsRunner.getJobManager()
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
            runningTask = runningTasks.get(runner);
            if (runningTask != null) {
                runningTasks.remove(runner);
                runningTask.setProgress(1.0);
                runningTask.setState(State.FINISHED);
                fireChanged(runningTask);
            }
            break;
        default:
            break;
        }
    }

    private void stateChanged(Job job, Status state) {
        JobsRunner runner = (JobsRunner) JobsRunner.getJobManager()
                .currentJob();
        JobInfo runningJob;
        switch (state) {
        case STARTED:
            runningJob = JobManager.getDefault().get(job);
            if (runningJob != null) {
                runningJobs.put(runner, runningJob);
                runningJob.setState(State.RUNNING);
                fireChanged(runningJob);
            }
            break;
        case STOPPED:
            runningJob = runningJobs.get(runner);
            if (runningJob != null) {
                runningJobs.remove(runner);
                runningJob.setState(State.FINISHED);
                fireChanged(runningJob);
            }
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

    public void aborted(Job job) {
        JobInfo info = JobManager.getDefault().get(job);
        info.setState(State.ABORTED);
    }

    public void failed(Job job) {
        JobInfo info = JobManager.getDefault().get(job);
        info.setState(State.FAILED);
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
