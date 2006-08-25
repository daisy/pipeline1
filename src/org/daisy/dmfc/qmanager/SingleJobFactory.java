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
