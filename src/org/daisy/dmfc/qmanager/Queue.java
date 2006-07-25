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
	
	private Queue(){
		this.linkedListJobs= new LinkedList();
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
	 * @param index (int)
	 */
	public void moveUp(int index){
		Job job = (Job)linkedListJobs.get(index);
		this.selectedJobIndex=index;
		System.out.println("Move up starting index = " + selectedJobIndex);
		deleteFromQueue(index);
		decrementSelectedJobIndex();
		this.linkedListJobs.add(selectedJobIndex, job);
		System.out.println("Now moved up to index = " + selectedJobIndex);
	}
	
	
	/**Move down one index in linked list
	 * @param index (int)
	 */
	public void moveDown(int index){
		Job job = (Job)linkedListJobs.get(index);
		this.selectedJobIndex=index;
		System.out.println("Move down starting index = " + selectedJobIndex);
		deleteFromQueue(index);
		incrementSelectedJobIndex();
		this.linkedListJobs.add(selectedJobIndex, job);
		System.out.println("Now moved down to index = " + selectedJobIndex);
	}
	
	/**
	 * 
	 * @param index
	 */
	public void deleteFromQueue(int index){
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
	
	public int getSizeOfQueue(){
		return this.linkedListJobs.size();
		
	}
}
