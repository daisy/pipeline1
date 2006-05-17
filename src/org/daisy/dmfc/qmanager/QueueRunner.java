package org.daisy.dmfc.qmanager;


import java.util.Iterator;
import java.util.LinkedList;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.exception.TransformerRunException;




/**
 * For each job (script)in the Queue, calls the execute method in the DMFCCore
 * @author Laurie Sherve
 *
 */

public class QueueRunner {
	
	private Queue queue;
	private DMFCCore dmfc;
	ScriptHandler scriptHandler;
	private EventListener ev;
	private InputListener il;
	
	public QueueRunner(DMFCCore dmfc){
		this.queue=Queue.getInstance();
		this.dmfc=dmfc;
	}
	
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
	 *@throws ScriptException on execute()
	 */
		public void execute()throws ScriptException{
	//public void start(){
			
			//walk through the queue and return jobs
			LinkedList jobList = queue.getLinkedListJobs();
			int count = 0;
			
			Iterator it = jobList.iterator();
			
			while(it.hasNext()){
				//get the Job from the Queue
				Job job = (Job)it.next();
				scriptHandler = job.getScript();
				
				//add the input and output files to the script
				//actually, this only returns if the parameters are present in the script...
				scriptHandler.setProperty("input", job.getInputFile().getPath());
				scriptHandler.setProperty("outputPath", job.getOutputFile().getPath());
				
				//List list = scriptHandler.getTransformerInfoList();
				
				try{	
					scriptHandler.execute();
				}
				catch(ScriptException e){
					throw new ScriptException(e.getMessage(), e);
				}
				

		}
	}
}
	

