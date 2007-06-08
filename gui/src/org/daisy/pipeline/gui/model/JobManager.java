/*
 * DAISY Pipeline GUI Copyright (C) 2006 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.gui.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.daisy.pipeline.core.script.Job;

public class JobManager implements Iterable {
    private static JobManager _default = new JobManager();

    private final List<JobInfo> jobs;
    private List<IJobManagerListener> listeners = new ArrayList<IJobManagerListener>();

    public JobManager() {
        jobs = new LinkedList<JobInfo>();
    }

    public static JobManager getDefault() {
        return _default;
    }

    public void add(int index, Job job) {
        add(index, new JobInfo(getNumberedName(job), job));
    }

    public void add(int index, JobInfo info) {
        jobs.add(index, info);
        fireJobsChanged(new JobInfo[] { info }, JobManagerEvent.Type.ADD, index);
    }

    public boolean add(Job job) {
        return add(new JobInfo(getNumberedName(job), job));
    }

    public boolean add(JobInfo info) {
        boolean res = jobs.add(info);
        fireJobsChanged(new JobInfo[] { info }, JobManagerEvent.Type.ADD);
        return res;
    }

    public boolean addAll(Collection<? extends JobInfo> c) {
        return addAll(-1, c);
    }

    public boolean addAll(int index, Collection<? extends JobInfo> c) {
        boolean modified = jobs.addAll((index == -1) ? jobs.size() : index, c);
        if (modified) {
            fireJobsChanged(c.toArray(new JobInfo[c.size()]),
                    JobManagerEvent.Type.ADD, index);
        }
        return modified;
    }

    public void clear() {
        jobs.clear();
        fireJobsChanged(jobs.toArray(new JobInfo[jobs.size()]),
                JobManagerEvent.Type.REMOVE);
    }

    /**
     * Note: this comparator imposes orderings that are inconsistent with
     * equals.
     * 
     * @return
     */
    public Comparator<JobInfo> createComparator() {
        return new Comparator<JobInfo>() {
            public int compare(JobInfo o1, JobInfo o2) {
                int i1 = jobs.indexOf(o1);
                int i2 = jobs.indexOf(o2);
                return i1 - i2;
            }
        };
    }

    public JobInfo get(int index) {
        return jobs.get(index);
    }

    public JobInfo get(Job job) {
        int index = indexOf(job);
        if (index != -1) {
            return get(index);
        }
        return null;
    }

    public int indexOf(Object object) {
        if (object instanceof Job) {
            int index = 0;
            for (JobInfo info : jobs) {
                if (info.getJob().equals(object)) {
                    return index;
                }
                index++;
            }
            return -1;
        } else {
            return jobs.indexOf(object);
        }
    }

    public boolean isEmpty() {
        return jobs.isEmpty();
    }

    public Iterator iterator() {
        return jobs.listIterator();
    }

    public void moveDown(Object job) {
        int index = indexOf(job);
        if (index != -1 && index != jobs.size() - 1) {
            move(index, index + 1);
        }
    }

    public void moveToBottom(Object job) {
        int index = indexOf(job);
        if (index != -1 && index != jobs.size() - 1) {
            move(index, jobs.size() - 1);
        }
    }

    public void moveToTop(Object job) {
        int index = indexOf(job);
        if (index > 0) {
            move(index, 0);
        }
    }

    public void moveUp(Object job) {
        int index = indexOf(job);
        if (index > 0) {
            move(index, index - 1);
        }
    }

    public void moveTo(Object job, int newIndex) {
        int oldIndex = indexOf(job);
        if (oldIndex != -1) {
            move(oldIndex, newIndex);
        }
    }

    public void move(int oldIndex, int newIndex) {
        if (oldIndex != newIndex) {
            JobInfo jobInfo = jobs.get(oldIndex);
            jobs.remove(oldIndex);
            jobs.add(newIndex, jobInfo);
            fireJobsChanged(new JobInfo[] { jobInfo },
                    JobManagerEvent.Type.UPDATE);
        }
    }

    public JobInfo remove(int index) {
        JobInfo info = jobs.remove(index);
        fireJobsChanged(new JobInfo[] { info }, JobManagerEvent.Type.REMOVE);
        return info;
    }

    public boolean remove(Object job) {
        int index = indexOf(job);
        if (index != -1) {
            JobInfo info = jobs.remove(index);
            fireJobsChanged(new JobInfo[] { info }, JobManagerEvent.Type.REMOVE);
        }
        return (index != -1);
    }

    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        List<JobInfo> removed = new ArrayList<JobInfo>(c.size());
        for (Object object : c) {
            int index = indexOf(object);
            if (index != -1) {
                JobInfo info = jobs.remove(index);
                removed.add(info);
                modified = true;
            }
        }
        if (modified) {
            fireJobsChanged(removed.toArray(new JobInfo[removed.size()]),
                    JobManagerEvent.Type.REMOVE);
        }
        return modified;
    }

    public int size() {
        return jobs.size();
    }

    public JobInfo[] toArray() {
        return jobs.toArray(new JobInfo[jobs.size()]);
    }

    public List<JobInfo> toList() {
        return Arrays.asList(toArray());
    }

    public Job[] toJobArray() {
        Job[] res = new Job[jobs.size()];
        int i = 0;
        for (JobInfo info : jobs) {
            res[i++] = info.getJob();
        }
        return res;
    }

    public List<Job> toJobList() {
        return Arrays.asList(toJobArray());
    }

    public void addJobsManagerListener(IJobManagerListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeJobsManagerListener(IJobManagerListener listener) {
        listeners.remove(listener);
    }

    private void fireJobsChanged(JobInfo[] jobs, JobManagerEvent.Type type) {
        fireJobsChanged(jobs, type, -1);
    }

    private void fireJobsChanged(JobInfo[] jobs, JobManagerEvent.Type type,
            int index) {
        JobManagerEvent event = new JobManagerEvent(this, jobs, index, type);
        for (IJobManagerListener listener : listeners) {
            listener.jobManagerChanged(event);
        }
    }

    private String getNumberedName(Job job) {
        String name = job.getScript().getNicename();
        int count = 1;
        for (JobInfo info : jobs) {
            if (info.getJob().getScript().equals(job.getScript())) {
                count++;
            }
        }
        if (count > 1) {
            name += " (" + count + ")";
        }
        return name;
    }
}
