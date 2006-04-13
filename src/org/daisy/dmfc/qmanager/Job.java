package org.daisy.dmfc.qmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.script.Task;
import org.daisy.dmfc.exception.ScriptException;


/**
 * <p>Business object containing Scripthandler and parameters from GUI
 * Parameters are the input file(s(sets)) and output file(s(sets))
 * entered by the user.</p>
 * <p>All functionality of the ScriptHandler scripts are available
 * through the ScriptHandler object.</p>
 * @author Laurie Sherve
 * @author Linus Ericson
 */

public class Job {

	private int status = Status.WAITING;
	private File inputFile;
	private File outputFile;
	private ScriptHandler script;
	
	
	
	public Job(){}
	
	
	/**
	 * A job refers to the script to be run and identifies 
	 * the input and output file or directories.
	 * This business object has no functionality.
	 * Information on the transformers in a script is accessed through the ScriptHandler 
	 * object.
	 * 
	 * @param _inputFile File
	 * @param _outputFile File
	 * @param _script File
	 */
	public Job(File _inputFile, File _outputFile, ScriptHandler _script){
		this.inputFile=_inputFile;
		this.outputFile=_outputFile;
		this.script= _script;
		
;	}	
	
	/**
	 * get methods for private member variables
	 */
	public int getStatus() {
		return this.status;
	}

	public File getInputFile() {
		return this.inputFile;
	}

	public File getOutputFile() {
		return this.outputFile;
	}

	public ScriptHandler getScript() {
		return this.script;
	}
	
	/**
	 * set methods for private member variables
	 */
	
	public void setInputFile(File input){
		this.inputFile=input;
	}
	public void setOutputFile(File output){
		this.outputFile=output;
	}
	public void setScript(ScriptHandler _script){
		this.script=_script;
	}
	public void setStatus(int _status){
		this.status=_status;
	}

	
	
	//Other information about the script /job
	public List getAllTasksInScript(){
		return this.script.getTasks();
	}
	
	public List getTransformerInfo(){
		return this.script.getTransformerInfoList();
	}
	
	public List getTransformersInScript() throws ScriptException{
		List tasks = getAllTasksInScript();
		List transformerNames = new ArrayList();
	
		for (Iterator it = tasks.iterator(); it.hasNext(); ) {
			Task task = (Task)it.next();
			String name = task.getName();				
			if (name == null) {
			    throw new ScriptException("TRANSFORMER_NOT_KNOWN" + task.getName());
			}
			else{
				tasks.add(name);
			}
			name = null;
		}
		return transformerNames;
	}
	

	public String toString(){
		return "input file name " + this.inputFile.getAbsolutePath() + "   OutputFileName " + this.outputFile.getPath();
	}
}
