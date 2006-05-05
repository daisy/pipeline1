package org.daisy.dmfc.gui.jface;


import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.Queue;

/**
 * Class that plays the role of the domain model tableViewer for 
 * the Jobs table.
 * Information may come from a data store, in this
 * case, the list is the Queue of jobs
 
 */

public class JobList {


	private LinkedList jobList;
	private Set changeListeners = new HashSet();

	
	/**
	 * Constructor
	 */
	public JobList() {
		super();
		this.initData();
	}
	
	/*
	 * Initialize the table data.
	
	 */
	private void initData() {
		jobList = Queue.getInstance().getLinkedListJobs();
	};


	
	/**
	 * Return the job queue
	 */
	public LinkedList getJobs() {
		return jobList;
	}
	
	/**
	 * Add a new job to queue
	 */
	public void addJob(Job job) {
		Queue.getInstance().addJobToQueue(job);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IJobListViewer) iterator.next()).addJob(job);
	}

	/**
	 * @param job
	 */
	public void removeJob(Job job) {
		Queue cue = Queue.getInstance();
		cue.deleteFromQueue(cue.getPlaceInQueue(job));
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IJobListViewer) iterator.next()).removeJob(job);
	}

	/**
	 * @param job
	 */
	public void jobChanged(Job job) {
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IJobListViewer) iterator.next()).updateJob(job);
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(IJobListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(IJobListViewer viewer) {
		changeListeners.add(viewer);
	}

}
