package org.daisy.dmfc.qmanager;

import java.util.Iterator;
import java.util.LinkedList;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.exception.ScriptException;




/**
 * For each job in the Queue, calls the execute method in the ScriptHandler
 * @author Laurie Sherve
 *
 */

public class QueueRunner  {
	
	private Queue queue;
	
	public QueueRunner(){
		this.queue=Queue.getInstance();
	}
	
	
	/**
	 * To run the Queue:
	 * Each Queue has many jobs
	 * Each job has a script
	 * Each script has one-to-many tasks 
	 * Each task is executed by the ScriptHandler
	 *
	 *@throws ScriptException on execute()
	 */
	public void executeJobsInQueue()throws ScriptException{
		
		//walk through the queue and return jobs
		LinkedList jobList = queue.getLinkedListJobs();
		Iterator it = jobList.iterator();
		
		while(it.hasNext()){
			//get the Job from the Queue
			Job job = (Job)it.next();
			
			//get the task script
			ScriptHandler script = job.getScript();
			
			//fill in the parameters in each job script
			/**
			 * @todo how to determine the "name" property for input and output?
			 * The value should be correct
			 */
			script.setProperty(script.getName(), job.getInputFile().getName());
			script.setProperty(script.getName(), job.getOutputFile().getName());
			
			//execute the script
			script.execute();
			
		}
	}
}
