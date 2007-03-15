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
        fireJobsChanged(new Job[] { job }, JobManagerEvent.Type.ADD, index);
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
                    JobManagerEvent.Type.ADD, index);
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
        if (index != -1 && index != jobs.size() - 1) {
            move(index, index + 1);
        }
    }

    public void moveToBottom(Job job) {
        int index = jobs.indexOf(job);
        if (index != -1 && index != jobs.size() - 1) {
            move(index, jobs.size() - 1);
        }
    }

    public void moveToTop(Job job) {
        int index = jobs.indexOf(job);
        if (index > 0) {
            move(index, 0);
        }
    }

    public void moveUp(Job job) {
        int index = jobs.indexOf(job);
        if (index > 0) {
            move(index, index - 1);
        }
    }

    public void moveTo(Job job, int newIndex) {
        int oldIndex = jobs.indexOf(job);
        if (oldIndex != -1) {
            move(oldIndex, newIndex);
        }
    }

    public void move(int oldIndex, int newIndex) {
        if (oldIndex != newIndex) {
            Job job = jobs.get(oldIndex);
            jobs.remove(oldIndex);
            jobs.add(newIndex, job);
            fireJobsChanged(new Job[] { job }, JobManagerEvent.Type.UPDATE);
        }
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

    public Job[] toArray() {
        return jobs.toArray(new Job[jobs.size()]);
    }

    public void addJobsManagerListener(IJobManagerListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeJobsManagerListener(IJobManagerListener listener) {
        listeners.remove(listener);
    }

    private void fireJobsChanged(Job[] jobs, JobManagerEvent.Type type) {
        fireJobsChanged(jobs, type, -1);
    }

    private void fireJobsChanged(Job[] jobs, JobManagerEvent.Type type,
            int index) {
        JobManagerEvent event = new JobManagerEvent(this, jobs, index, type);
        for (IJobManagerListener listener : listeners) {
            listener.jobManagerChanged(event);
        }
    }
}
