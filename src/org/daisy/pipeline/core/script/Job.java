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

import java.util.LinkedHashMap;
import java.util.Map;

import org.daisy.pipeline.core.script.datatype.DatatypeException;

/**
 * A class used for setting parameters before running a script.
 * @author Linus Ericson
 */
public class Job {

	private Script mScript;
	private Map<String,JobParameter> mParameters; 
	
	/**
	 * Creates a new Job object from a Script.
	 * @param script
	 */
	public Job(Script script) {
		this.mScript = script;
		this.mParameters = new LinkedHashMap<String,JobParameter>();
		for (ScriptParameter param : script.getParameters().values()) {
			try {
				mParameters.put(param.getName(), new JobParameter(param.getName(), param.getValue(), this, param));
			} catch (ScriptValidationException e) {
				// This should not happen since StringProperty has
				// an empty validation method.
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets the script object
	 * @return
	 */
	public Script getScript() {
		return mScript;
	}
	
	/**
	 * Gets a specific ScriptParameter. If the specified ScriptParameter is
	 * not found, null is returned.
	 * @param name the name of the ScriptParameter to get
	 * @return a ScriptParameter, or null
	 */
	public ScriptParameter getScriptParameter(String name) {
		return mScript.getParameter(name);
	}
	
	/**
	 * Sets a parameter value.
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @throws DatatypeException
	 */
	public void setParameterValue(String name, String value) throws DatatypeException {
		JobParameter jobParameter = this.getJobParameter(name);
		jobParameter.setValue(value);		
	}
	
	/**
	 * Gets the value of a parameter.
	 * @param name the name of the parameter
	 * @return the value of the parameter or null if the parameter was not set
	 */
	public String getParameterValue(String name) {
		AbstractProperty prop = mParameters.get(name);
		if (prop != null) {
			return prop.getValue();
		}
		return null;
	}
	
	/**
	 * Gets the JobParameter for a specified parameter.
	 * @param name the name of the parameter
	 * @return a JobParameter, or null if no parameter with the specified name exists
	 */
	public JobParameter getJobParameter(String name) {
		return mParameters.get(name);
	}
	
	/**
	 * Gets a map containing all parameters in this Job
	 * @return
	 */
	public Map<String,JobParameter> getJobParameters() {
		return mParameters;
	}
	
	/**
	 * Have all required script parameters been set in this Job?
	 * @return
	 */
	public boolean allRequiredParametersSet() {
		boolean result = true;
		for (ScriptParameter parameter : mScript.getRequiredParameters().values()) {
			if (!this.getJobParameter(parameter.getName()).hasChanged()) {
				result = false;
			}
		}
		return result;
	}
	
}
