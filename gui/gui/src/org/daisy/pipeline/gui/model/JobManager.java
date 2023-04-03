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
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.daisy.pipeline.core.script.Job;
import org.daisy.util.execution.State;

/**
 * Manages a list of Pipeline jobs. This manager is used as the data model of
 * the jobs view.
 * 
 * @author Romain Deltour
 * 
 */
public class JobManager implements Iterable<JobInfo> {
	private static JobManager _default = new JobManager();

	/**
	 * Returns a default unique instance of the JobManager for use in a
	 * singleton-like pattern.
	 * 
	 * @return a default and unique instance of the job manager
	 */
	public static JobManager getDefault() {
		return _default;
	}

	/** The underlying list of jobs */
	private final List<JobInfo> jobs;
	/** The list of listeners to this manager */
	private List<IJobManagerListener> listeners = new ArrayList<IJobManagerListener>();

	/**
	 * Creates a new instance of this manager.
	 */
	public JobManager() {
		jobs = new LinkedList<JobInfo>();
	}

	/**
	 * Inserts the given pipeline job as a new job info at the given index in
	 * this manager.
	 * 
	 * @param index
	 *            the index at which to insert the job
	 * @param job
	 *            the pipeline job to insert
	 * @see JobInfo
	 */
	public void add(int index, Job job) {
		add(index, new JobInfo(getNumberedName(job), job));
	}

	/**
	 * Inserts the given job at the given index in this manager.
	 * 
	 * @param index
	 *            the index at which to insert the job
	 * @param info
	 *            the job info to insert
	 */
	public void add(int index, JobInfo info) {
		jobs.add(index, info);
		fireJobsChanged(new JobInfo[] { info }, JobManagerEvent.Type.ADD, index);
	}

	/**
	 * Appends the given pipeline job as a new job info to this manager.
	 * 
	 * @param job
	 *            the pipeline job to append
	 * @return <code>true</code> if the job was correctly added
	 * @see JobInfo
	 */
	public boolean add(Job job) {
		return add(new JobInfo(getNumberedName(job), job));
	}

	/**
	 * Appends the given job to this manager.
	 * 
	 * @param info
	 *            the job to append
	 * @return <code>true</code> if the job was correctly added
	 */
	public boolean add(JobInfo info) {
		boolean res = jobs.add(info);
		fireJobsChanged(new JobInfo[] { info }, JobManagerEvent.Type.ADD);
		return res;
	}

	/**
	 * Append the given jobs to this manager
	 * 
	 * @param c
	 *            collection of jobs to append
	 * @return <code>true</code> if the jobs were correctly added
	 */
	public boolean addAll(Collection<? extends JobInfo> c) {
		return addAll(-1, c);
	}

	/**
	 * Insert the given jobs at the given index in this manager
	 * 
	 * @param index
	 *            the index at which to insert the collection of jobs
	 * @param c
	 *            a collection of job to add to this manager
	 * @return <code>true</code> if the jobs was correctly added
	 */
	public boolean addAll(int index, Collection<? extends JobInfo> c) {
		boolean modified = jobs.addAll((index == -1) ? jobs.size() : index, c);
		if (modified) {
			fireJobsChanged(c.toArray(new JobInfo[c.size()]),
					JobManagerEvent.Type.ADD, index);
		}
		return modified;
	}

	/**
	 * Adds the given listener to set of of listeners to {@link JobManagerEvent}s
	 * raised by this manager.
	 * 
	 * @param listener
	 *            a new listener to this job manager
	 */
	public void addJobsManagerListener(IJobManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Empties this manager from its jobs
	 */
	public void clear() {
		jobs.clear();
		fireJobsChanged(jobs.toArray(new JobInfo[jobs.size()]),
				JobManagerEvent.Type.REMOVE);
	}

	/**
	 * Creates a comparator for jobs contained in this manager. The created
	 * comparator uses the jobs index to compares the jobs.
	 * 
	 * <p>
	 * Note: this comparator imposes orderings that are inconsistent with
	 * equals.
	 * </p>
	 * 
	 * @return a new comparator for the jobs contained in this manager
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

	/**
	 * Returns the job at the given index
	 * 
	 * @param index
	 *            an index in this manager
	 * @return the job at the given index
	 */
	public JobInfo get(int index) {
		return jobs.get(index);
	}

	/**
	 * Returns the job info representing the given pipeline job in this manager
	 * 
	 * @param job
	 *            a pipeline job
	 * @return the job info representing <code>job</code> in this manager or
	 *         <code>null</code> if it has not been found
	 */
	public JobInfo get(Job job) {
		int index = indexOf(job);
		if (index != -1) {
			return get(index);
		}
		return null;
	}

	/**
	 * Returns the list of jobs the state of which is contained in the given
	 * state enumeration
	 * 
	 * @param states
	 *            an enumeration of execution state
	 * @return the list of the jobs in this manager that are in a state
	 *         contained in <code>states</code>
	 */
	public List<JobInfo> getJobsByState(EnumSet<State> states) {
		ArrayList<JobInfo> res = new ArrayList<JobInfo>();
		for (JobInfo jobInfo : jobs) {
			if (states.contains(jobInfo.getSate())) {
				res.add(jobInfo);
			}
		}
		return res;
	}

	/**
	 * Get the name of the given job with a number postfix according to its date
	 * of arrival in this manager.
	 * 
	 * @param job
	 *            a job in this manager
	 * @return a string uniquely representing this job in this manager
	 */
	private String getNumberedName(Job job) {
		String name = job.getScript().getNicename();
		int count = 1;
		for (JobInfo info : jobs) {
			if (info.getJob().getScript().equals(job.getScript())) {
				count++;
			}
		}
		if (count > 1) {
			name += " (" + count + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return name;
	}

	/**
	 * Returns the index of the given object in this manager
	 * 
	 * @param object
	 *            an object (should be a {@link Job} or {@link JobInfo}
	 * @return the index of the given object in this manager or <code>-1</code>
	 *         if the manager does not contain this object
	 */
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

	/**
	 * Whether this manager contains jobs
	 * 
	 * @return <code>true</code> if and only if this manager contains no jobs
	 */
	public boolean isEmpty() {
		return jobs.isEmpty();
	}

	/**
	 * Returns an iterator of the jobs in this manager/
	 */
	public Iterator<JobInfo> iterator() {
		return jobs.listIterator();
	}

	/**
	 * Moves the object at item <code>oldIndex</code> to the index
	 * <code>newIndex</code>.
	 * 
	 * @param oldIndex
	 *            an index in this manager
	 * @param newIndex
	 *            the new index of the object at <code>oldIndex</code>
	 */
	public void move(int oldIndex, int newIndex) {
		if (oldIndex != newIndex) {
			JobInfo jobInfo = jobs.get(oldIndex);
			jobs.remove(oldIndex);
			jobs.add(newIndex, jobInfo);
			fireJobsChanged(new JobInfo[] { jobInfo },
					JobManagerEvent.Type.UPDATE);
		}
	}

	/**
	 * Move the given job one index down in this manager
	 * 
	 * @param job
	 *            A job in this manager
	 */
	public void moveDown(Object job) {
		int index = indexOf(job);
		if ((index != -1) && (index != jobs.size() - 1)) {
			move(index, index + 1);
		}
	}

	/**
	 * Move the given job to the given index in this manager
	 * 
	 * @param job
	 *            A job in this manager
	 * @param newIndex
	 *            the new index at which to move the given job
	 */
	public void moveTo(Object job, int newIndex) {
		int oldIndex = indexOf(job);
		if (oldIndex != -1) {
			move(oldIndex, newIndex);
		}
	}

	/**
	 * Move the given job to the end of this manager
	 * 
	 * @param job
	 *            A job in this manager
	 */
	public void moveToBottom(Object job) {
		int index = indexOf(job);
		if ((index != -1) && (index != jobs.size() - 1)) {
			move(index, jobs.size() - 1);
		}
	}

	/**
	 * Move the given job at the first position in this manager
	 * 
	 * @param job
	 *            A job in this manager
	 */
	public void moveToTop(Object job) {
		int index = indexOf(job);
		if (index > 0) {
			move(index, 0);
		}
	}

	/**
	 * Move the given job one index up in this manager
	 * 
	 * @param job
	 *            A job in this manager
	 */
	public void moveUp(Object job) {
		int index = indexOf(job);
		if (index > 0) {
			move(index, index - 1);
		}
	}

	/**
	 * Removes the job at the given index in this manager and returns it.
	 * 
	 * @param index
	 *            the index of the job to remove
	 * @return the removed job
	 */
	public JobInfo remove(int index) {
		JobInfo info = jobs.remove(index);
		fireJobsChanged(new JobInfo[] { info }, JobManagerEvent.Type.REMOVE,
				index);
		return info;
	}

	/**
	 * Removes the given object (a {@link Job} or {@link JobInfo}) from this
	 * manager
	 * 
	 * @param job
	 *            the job to remove
	 * @return <code>true</code> if the given object was contained in this
	 *         manager and correctly removed
	 */
	public boolean remove(Object job) {
		int index = indexOf(job);
		if (index != -1) {
			remove(index);
		}
		return (index != -1);
	}

	/**
	 * Removes the given collection of jobs from this manager
	 * 
	 * @param c
	 *            a collection of jobs to remove from this manager
	 * @return <code>true</code> if and only if the manager was modified by
	 *         the call to this method
	 */
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		List<JobInfo> removed = new ArrayList<JobInfo>(c.size());
		int minIndex = -1;
		for (Object object : c) {
			int index = indexOf(object);
			if (index != -1) {
				JobInfo info = jobs.remove(index);
				removed.add(info);
				modified = true;
				minIndex = (minIndex == -1) ? index : Math.min(minIndex, index);
			}
		}
		if (modified) {
			fireJobsChanged(removed.toArray(new JobInfo[removed.size()]),
					JobManagerEvent.Type.REMOVE, minIndex);
		}
		return modified;
	}

	/**
	 * Removes the given listener from the set of listeners to
	 * {@link JobManagerEvent}s raised by this manager.
	 * 
	 * @param listener
	 *            a listener to this job manager
	 */
	public void removeJobsManagerListener(IJobManagerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Returns the number of jobs contained in this manager.
	 * 
	 * @return the number of jobs contained in this manager
	 */
	public int size() {
		return jobs.size();
	}

	/**
	 * Returns the content of this manager as an array of {@link JobInfo}s.
	 * 
	 * @return the content of this manager as an array of {@link JobInfo}s
	 */
	public JobInfo[] toArray() {
		return jobs.toArray(new JobInfo[jobs.size()]);
	}

	/**
	 * 
	 * Returns the content of this manager as an array of Pipeline {@link Job}s.
	 * 
	 * @return the content of this manager as an array of Pipeline {@link Job}s
	 */
	public Job[] toJobArray() {
		Job[] res = new Job[jobs.size()];
		int i = 0;
		for (JobInfo info : jobs) {
			res[i++] = info.getJob();
		}
		return res;
	}

	/**
	 * Returns the content of this manager as a new list of Pipeline {@link Job}s.
	 * 
	 * @return the content of this manager as a new list of Pipeline {@link Job}s
	 */
	public List<Job> toJobList() {
		return Arrays.asList(toJobArray());
	}

	/**
	 * Returns the content of this manager as a new list of {@link JobInfo}s.
	 * 
	 * @return the content of this manager as a new list of {@link JobInfo}s
	 */
	public List<JobInfo> toList() {
		return Arrays.asList(toArray());
	}
}
