/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005  Daisy Consortium
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

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
	private static Queue _instance = new Queue();
	
	
	//Singleton implementation
	public static Queue getInstance(){
	      // if (_instance == null) _instance = new Queue();
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
		System.out.println("Prior to delete, the size in the cue is " + linkedListJobs.size());
		linkedListJobs.remove(index);
		System.out.println("the index to delete " + index);
		System.out.println(" After delete, the size in the cue is " + linkedListJobs.size());
		
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
	 * @return Job
	 */
	
	public Job getNextJobInQueue(int placeInQueue){
		return editJob(placeInQueue);
	}
	
	public int getSizeOfQueue(){
		return this.linkedListJobs.size();
		
	}
}