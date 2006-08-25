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
import java.util.Iterator;
import java.util.List;

/**
 * <p>Batch processing of many chosen input files
 * User chooses one type of conversion or script
 * User selects many input files from a filtered list of files</p>
 * 
 * <p>A Job object is created for each input file consisting 
 * of the chosen script, each input file, and a directory path.
 * The Job is added to the queue for further processing</p>
 * 
 * @author Laurie Sherve
 *
 */

public class BatchJobFactory extends SingleJobFactory{
	
	private List selectedFiles;
	
	public BatchJobFactory(){
		super();
	}
	
	public void setList(List selected){
		this.selectedFiles=selected;
	}
	
	/**
	 * In a batch conversion, the user has two choices of output directories
	 * 1. In the input file path
	 * 2. In a path of their choosing
	 * 
	 * The output directory is constructed as follows:
	 * <em>inputfilepath/name of input file/all files created</em>
	 * where the name of the input file becomes the name of a directory
	 *
	 * @param _output File, directory for the output
	 */
	public void setOutputDocument(File _output){
		
		this.output= new File(_output.getName() + File.pathSeparatorChar + input.getName()  + File.pathSeparatorChar);
	}
	
	/**
	 * Add each job to the Queue.
	 *
	 */
	public void addConversionsToQueue(){
		
		Iterator it =  selectedFiles.iterator();
		while (it.hasNext()){	
			this.input = (File)it.next();
			Job job = new Job(input, output, script);
			queue.addJobToQueue(job);
		}
	}
}
