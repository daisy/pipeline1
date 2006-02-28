package org.daisy.dmfc.qmanager;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptHandler;


/**
 * <p>A Job object is created consisting 
 * of the chosen script, an input file, and an output directory path.
 * The Job is added to the Queue for further processing</p>
 * 
 * @author Laurie Sherve
 *
 */

public class SingleJobFactory implements IJobFactory {

	protected Job job;
	protected Queue queue;
	protected File input;
	protected File output;
	protected ScriptHandler script;
	
	public SingleJobFactory(){
		queue= Queue.getInstance();
	}
	
	public void setInputDocument(File _input){
		this.input=_input;
	}
	
	public void setOutputDocument(File _output){
		this.output=_output;
	}
	
	public void setScriptHandler(ScriptHandler _script){
		this.script=_script;
	}
	
	public void createJobAndAddToQueue(){
		job=new Job(input, output, script);
		queue.addJobToQueue(job);
	}
	
}
