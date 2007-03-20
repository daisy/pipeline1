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
package org.daisy.dmfc.core.script;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A class representing the contents of a task script.
 * @author Linus Ericson 
 */
public class Script {
	private String mName;
	private String mNicename;
	private String mDescription;
	private URI mDocumentation;
	
	private Map<String,AbstractProperty> mProperties;	
	
	private List<Task> tasks;
	
	/**
	 * Constructor. The script is initially empty and needs to be filled with
	 * properties and tasks.
	 */
	Script() {
		this.mProperties = new LinkedHashMap<String,AbstractProperty>();
		this.tasks = new LinkedList<Task>();
	}

	/**
	 * Gets the description of the script
	 * @return
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * Gets the documentation URI of this script.
	 * @return 
	 */
	public URI getDocumentation() {
		return  mDocumentation;
	}
	
	/**
	 * Gets the name of the script
	 * @return
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Gets the nicename of the script. This could be presended in a GUI.
	 * @return
	 */
	public String getNicename() {
		return mNicename;
	}

	/**
	 * Gets a Map containing all properties defined in this script.
	 * @return
	 */
	public Map<String, AbstractProperty> getProperties() {
		return mProperties;
	}
	
	/**
	 * Gets a Map containing all parameters defined in this script.
	 * This is a subset of all the properties.
	 * @return
	 */
	public Map<String, ScriptParameter> getParameters() {
		Map <String,ScriptParameter> parameters = new HashMap<String, ScriptParameter>();
		for (AbstractProperty property : mProperties.values()) {
			if (property instanceof ScriptParameter) {
				ScriptParameter param = (ScriptParameter)property;
				parameters.put(property.getName(), param);
			}
		}
		return parameters;
	}
	
	/**
	 * Gets a specific script parameter.
	 * @param name the name of the parameter
	 * @return a ScriptParameter, or null if the parameter was not found
	 */
	public ScriptParameter getParameter(String name) {
		return this.getParameters().get(name);
	}

	/**
	 * Gets a list of all tasks in the script.
	 * @return
	 */
	public List<Task> getTasks() {
		return tasks;
	}
	
	/**
	 * Sets the description of this script.
	 * @param description
	 */
	void setDescription(String description) {
		this.mDescription = description;
	}
	
	/**
	 * Sets the URI of the documentation of this script.
	 * @param description
	 */
	void setDocumentation(URI uri) {
		File test = new File(uri);
		if(!test.exists()||!test.canRead()) {
			System.err.println("Warning [in Script#setDocumentation]: Script documentation URI " + uri.toString() + " seems not to resolve");
		}
		this.mDocumentation = uri;
	}

	/**
	 * Sets the name of this script.
	 * @param name
	 */
	void setName(String name) {
		this.mName = name;
	}

	/**
	 * Sets the nicename of this script.
	 * @param nicename
	 */
	void setNicename(String nicename) {
		this.mNicename = nicename;
	}
	
	/**
	 * Adds a property.
	 * @param name the name of the property to add
	 * @param property the property itself
	 */
	void addProperty(String name, AbstractProperty property) {
		this.mProperties.put(name, property);
	}
		
	/**
	 * Adds a task
	 * @param task the task to add
	 */
	void addTask(Task task) {
		this.tasks.add(task);
	}
		
}
