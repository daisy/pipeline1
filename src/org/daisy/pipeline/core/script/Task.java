/*
 * Daisy Pipeline
 * Copyright (C) 2007  Daisy Consortium
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
package org.daisy.pipeline.core.script;

import java.util.HashMap;
import java.util.Map;

import org.daisy.pipeline.core.transformer.TransformerHandler;
import org.daisy.pipeline.core.transformer.TransformerInfo;

/**
 * A class representing a task in the script file.
 * @author Linus Ericson
 */
public class Task {
	private String name;
	private boolean interactive;
	private Map<String,TaskParameter> parameters;
	private TransformerHandler handler;
	
	/**
	 * Constructor.
	 * @param name the name of the task
	 * @param interactive
	 */
	Task(String name, boolean interactive) {
		this.name = name;
		this.interactive = interactive;
		this.parameters = new HashMap<String, TaskParameter>();
	}

	/**
	 * Can the transformer be run in interactive mode?
	 * @return
	 */
	public boolean isInteractive() {
		return interactive;
	}

	/**
	 * Gets the name of the task
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets a Map containing all task parameters
	 * @return
	 */
	public Map<String, TaskParameter> getParameters() {
		return parameters;
	}
	
	/**
	 * Adds a task parameter
	 * @param name the name if the parameter to add
	 * @param parameter the parameter itself
	 */
	/*package*/ void addParameter(String name, TaskParameter parameter) {
		this.parameters.put(name, parameter);
	}
	
	/**
	 * Sets the Transformerhandler to be associated with this task.
	 * @param handler
	 */
	/*package*/ void setTransformerHandler(TransformerHandler handler) {
		this.handler = handler;
	}
	
	/**
	 * Gets the TransformerHandler associated with this task.
	 * @return
	 */
	/*package*/ TransformerHandler getTransformerHandler() {
		return this.handler;
	}
	
	/**
	 * Gets the TransformerInfo associated with the transformer this task refers to.
	 * @return
	 */
	public TransformerInfo getTransformerInfo() {
		return this.handler;
	}
}
