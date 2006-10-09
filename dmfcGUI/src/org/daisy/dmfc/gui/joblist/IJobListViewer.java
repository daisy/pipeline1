package org.daisy.dmfc.gui.joblist;

import org.daisy.dmfc.qmanager.Job;





/*
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Jun 11, 2003 by lgauthier@opnworks.com
 *
 */

public interface IJobListViewer {
	
	/**
	 * Update the view to reflect the fact that a job was added 
	 * to the job list
	 * 
	 * @param task
	 */
	public void addJob(Job job);
	
	/**
	 * Update the view to reflect the fact that a task was removed 
	 * from the task list
	 * 
	 * @param task
	 */
	public void removeJob(Job job);
	
	/**
	 * Update the view to reflect the fact that one of the jobs
	 * was modified 
	 * 
	 * @param task
	 */
	public void updateJob(Job job);
}