package org.daisy.pipeline.gui.jobs.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JobManager implements Iterable {
    private static JobManager instance;

    private final List<Job> jobs;
    private List<IJobManagerListener> listeners = new ArrayList<IJobManagerListener>();

    private JobManager() {
        jobs = new LinkedList<Job>();
    }

    public static JobManager getInstance() {
        if (instance == null) {
            instance = new JobManager();
        }
        return instance;
    }

    public void add(int index, Job job) {
        jobs.add(index, job);
        fireJobsChanged(new Job[] { job }, JobManagerEvent.Type.ADD);
    }

    public boolean add(Job job) {
        boolean res = jobs.add(job);
        fireJobsChanged(new Job[] { job }, JobManagerEvent.Type.ADD);
        return res;
    }

    public boolean addAll(Collection<? extends Job> c) {
        boolean res = jobs.addAll(c);
        if (res) {
            fireJobsChanged(c.toArray(new Job[c.size()]),
                    JobManagerEvent.Type.ADD);
        }
        return res;
    }

    public boolean addAll(int index, Collection<? extends Job> c) {
        boolean res = jobs.addAll(index, c);
        if (res) {
            fireJobsChanged(c.toArray(new Job[c.size()]),
                    JobManagerEvent.Type.ADD);
        }
        return res;
    }

    public void clear() {
        jobs.clear();
        fireJobsChanged(jobs.toArray(new Job[jobs.size()]),
                JobManagerEvent.Type.REMOVE);
    }

    public Job get(int index) {
        return jobs.get(index);
    }

    public int indexOf(Job job) {
        return jobs.indexOf(job);
    }

    public boolean isEmpty() {
        return jobs.isEmpty();
    }

    public Iterator iterator() {
        return jobs.listIterator();
    }

    public void moveDown(Job job) {
        int index = jobs.indexOf(job);
        if (index > 0) {
            jobs.remove(index);
            jobs.add(index - 1, job);
        }
        fireJobsChanged(new Job[] { job }, JobManagerEvent.Type.UPDATE);
    }

    public void moveToBottom(Job job) {
        int index = jobs.indexOf(job);
        if (index > 0) {
            jobs.remove(index);
            jobs.add(0, job);
        }
        fireJobsChanged(new Job[] { job }, JobManagerEvent.Type.UPDATE);
    }

    public void moveToTop(Job job) {
        int index = jobs.indexOf(job);
        if (index != -1 && index != jobs.size() - 1) {
            jobs.remove(index);
            jobs.add(jobs.size(), job);
        }
        fireJobsChanged(new Job[] { job }, JobManagerEvent.Type.UPDATE);
    }

    public void moveUp(Job job) {
        int index = jobs.indexOf(job);
        if (index != -1 && index != jobs.size() - 1) {
            jobs.remove(index);
            jobs.add(index + 1, job);
        }
        fireJobsChanged(new Job[] { job }, JobManagerEvent.Type.UPDATE);
    }

    public Job remove(int index) {
        Job job = jobs.remove(index);
        fireJobsChanged(new Job[] { job }, JobManagerEvent.Type.REMOVE);
        return job;
    }

    public boolean remove(Job job) {
        boolean res = jobs.remove(job);
        if (res) {
            fireJobsChanged(new Job[] { job }, JobManagerEvent.Type.REMOVE);
        }
        return res;
    }

    public boolean removeAll(Collection<? extends Job> c) {
        boolean res = jobs.removeAll(c);
        if (res) {
            fireJobsChanged(c.toArray(new Job[c.size()]),
                    JobManagerEvent.Type.REMOVE);
        }
        return res;
    }

    public int size() {
        return jobs.size();
    }

    public void addJobsManagerListener(IJobManagerListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeJobsManagerListener(IJobManagerListener listener) {
        listeners.remove(listener);
    }

    private void fireJobsChanged(Job[] jobs, JobManagerEvent.Type type) {
        JobManagerEvent event = new JobManagerEvent(this, jobs, type);
        for (IJobManagerListener listener : listeners) {
            listener.jobManagerChanged(event);
        }
    }
}
