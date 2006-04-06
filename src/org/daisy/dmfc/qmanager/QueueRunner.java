package org.daisy.dmfc.qmanager;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.util.xml.validation.ValidationException;




/**
 * For each job (script)in the Queue, calls the execute method in the DMFCCore
 * @author Laurie Sherve
 *
 */

public class QueueRunner  {
	
	private Queue queue;
	private DMFCCore dmfc;
	private EventListener ev;
	private InputListener il;
	
	public QueueRunner(EventListener ev, InputListener il){
		this.queue=Queue.getInstance();
		try{
			this.dmfc=new DMFCCore(il, ev);
		}
		catch (DMFCConfigurationException e) {  
			//add error messages to be thrown to GUI
               e.printStackTrace();
		}
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
			ScriptHandler sh = null;
			File script = job.getScript();
			
				//script.setProperty("input", job.getInputFile().getName());
				//script.setProperty("outputDir", job.getOutputFile().getName());
				
				try{
					sh= dmfc.createScript(script);
//					sh.setProperty("input", job.getInputFile().getName());
					sh.setProperty("outputDir", job.getOutputFile().getName());
					dmfc.executeScript(sh);
				}
				catch(ValidationException ve){
					ve.getMessage();
					ve.printStackTrace();
				}
				catch(MIMEException me){
					me.getMessage();
					me.printStackTrace();
				}
				
				catch(ScriptException se){
					//add error messages to be thrown to GUI
					se.printStackTrace();
				}
			
			
		}
	}
}
