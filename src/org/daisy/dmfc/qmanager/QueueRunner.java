package org.daisy.dmfc.qmanager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.naming.ConfigurationException;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.dmfc.exception.ScriptException;




/**
 * For each job (script)in the Queue, calls the execute method in the DMFCCore
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
	 * Each job is/has a script
	 * Each script has one-to-many tasks 
	 * Each script is executed by the DMFCCore 
	 * (and each task is executed by the ScriptHandler)
	 * 
	 *@param il InputListener interface - for concrete web or local IL
	 *@param el EventListener interface - for concrete web or local EL
	 *@throws ScriptException on execute()
	 */
	public void executeJobsInQueue(InputListener il, EventListener el){
		
		/** @todo possibly put this in a properties file? Does this differ from script to script?*/
		Locale locale = new Locale("en", "US");
			
		//walk through the queue and return jobs
		LinkedList jobList = queue.getLinkedListJobs();
		Iterator it = jobList.iterator();
		
		while(it.hasNext()){
			//get the Job from the Queue
			Job job = (Job)it.next();
			
			//get the task script
			ScriptHandler script = job.getScript();
			
			try{
				DMFCCore dmfc = new DMFCCore(il, el);
				script.setProperty("input", job.getInputFile().getName());
				script.setProperty("outputDir", job.getOutputFile().getName());
				dmfc.executeScript(script);
			}
			catch(ScriptException se){
				//add error messages to be thrown to GUI
			}
			catch (DMFCConfigurationException e) {  
				//add error messages to be thrown to GUI
	               e.printStackTrace();
			}
			
		}
	}
}
