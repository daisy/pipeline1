package org.daisy.dmfc.qmanager;

import java.util.LinkedList;


/**
 *Really just a linked list of Job Objects
 *@author Laurie Sherve
 *@author Linus Ericson
 */


public class Queue {
	
	private LinkedList linkedListJobs ;
	private int currentJobIndex;
	private int selectedJobIndex;
	
	//for singleton
	private static Queue _instance;
	
	
	//Singleton implementation
	public static Queue getInstance(){
	       if (_instance == null) _instance = new Queue();
	       return _instance;  
	}
	
	//get and set methods
	
	public void setCurrentJobIndex(int jobRunning){
		this.currentJobIndex=jobRunning;
	}

	public int getCurrentJobIndex(){
		return this.currentJobIndex;
	}
	
	public LinkedList getLinkedListJobs(){
		return this.linkedListJobs;
	}
	
	public void incrementCurrentJobIndex(){
		this.currentJobIndex++;	
	}
	public void decrementSelectedJobIndex(){
		this.selectedJobIndex--;	
	}
	public void incrementSelectedJobIndex(){
		this.selectedJobIndex++;	
	}
	
	/**
	 * Move up one index in linked list
	 * @param Job
	 */
	public void moveUp(Job job){
		deleteFromQueue(job);
		incrementSelectedJobIndex();
		this.linkedListJobs.add(selectedJobIndex, job);
	}
	
	/**Move down one index in linked list
	 * @param Job
	 */
	public void moveDown(Job job){
		deleteFromQueue(job);
		decrementSelectedJobIndex();
		this.linkedListJobs.add(selectedJobIndex, job);
	}
	
	/**
	 * Delete a Job from the linked list
	 * 
	 * @param Job 
	 */
	public void deleteFromQueue(Job job){
		int index = getPlaceInQueue(job);
		linkedListJobs.remove(index);
	}
	/**
	 * returns a job in the queue for editing in the gui
	 * @param placeInQueue int
	 * @return Job object
	 */
	public Job editJob(int placeInQueue){
		return (Job) this.linkedListJobs.get(placeInQueue);	
	}
	
	/**
	 * 
	 * @param job Job object
	 * @return int
	 */
	public int getPlaceInQueue(Job job){
		return this.linkedListJobs.indexOf(job);
	}
	
	public void addJobToQueue(Job job){
		this.linkedListJobs.addLast(job);
		
	}
	
	/**
	 * Same result as editJob, just different reason for use.
	 * 
	 * @param placeInQueue
	 * @return
	 */
	
	public Job getNextJobInQueue(int placeInQueue){
		return editJob(placeInQueue);
	}
}
