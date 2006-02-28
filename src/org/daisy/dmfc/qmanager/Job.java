package org.daisy.dmfc.qmanager;

import java.io.File;

import org.daisy.dmfc.core.script.ScriptHandler;


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

	public Job (){
	}
	
	
	/**
	 * A job refers to the script to be run and identifies the input and output files or
	 * directories.
	 * This business object has no functionality.
	 * Information on the transformer in a script is accessed through the ScriptHandler 
	 * object.
	 * 
	 * @param _inputFile File
	 * @param _outputFile File
	 * @param _script ScriptHandler
	 */
	public Job(File _inputFile, File _outputFile, ScriptHandler _script){
		this.inputFile=_inputFile;
		this.outputFile=_outputFile;
		this.script=_script;
	}	
	
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
	
	
}
