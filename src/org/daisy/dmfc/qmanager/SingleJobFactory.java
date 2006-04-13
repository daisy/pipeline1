package org.daisy.dmfc.qmanager;

import java.io.File;
import java.util.List;

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
	//protected ScriptHandler script;
	protected ScriptHandler script;
	
	public SingleJobFactory(){
		queue= Queue.getInstance();
	}
	
	public void setInputDocument(File _input){
		this.input=_input;
	}
	
	/**
	 * * The output directory is constructed as follows:
	 * <em>inputfilepath/name of input file/all files created</em>
	 * where the name of the input file becomes the name of a directory
	 * @param _output File (Directory)
	 */
	public void setOutputDocument(File _output){
		this.output= new File(_output.getName() + File.pathSeparatorChar + input.getName()  + File.pathSeparatorChar);
	}
	
	public void setScriptHandler(ScriptHandler _script){
		this.script=_script;
	}
	
	public void createJobAndAddToQueue(){
		job=new Job(input, output, script);
		queue.addJobToQueue(job);
	}
	
	
}
