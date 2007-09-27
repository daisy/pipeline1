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

import org.daisy.pipeline.core.script.datatype.DatatypeException;

/**
 * A class representing a parameter in a Job.
 * 
 * @author Linus Ericson
 */
public class JobParameter extends StringProperty {

	private Job mJob;
	private ScriptParameter mParameter;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            the name of the parameter
	 * @param value
	 *            the initial value of the parameter
	 * @param job
	 *            the Job the JobParameter belongs to
	 * @param parameter
	 *            the ScriptParameter this JobParameter is associated with
	 * @throws ScriptValidationException
	 */
	public JobParameter(String name, String value, Job job,
			ScriptParameter parameter) throws ScriptValidationException {
		super(name, value);
		mJob = job;
		mParameter = parameter;
	}

	/**
	 * @return the Job this JobParameter belongs to
	 */
	public Job getJob() {
		return mJob;
	}

	/**
	 * @return the ScriptParamater this Job parameter is associated with
	 */
	public ScriptParameter getScriptParameter() {
		return mParameter;
	}

	/**
	 * Sets the value of the JobParameter
	 * 
	 * @param value
	 *            the new value
	 * @throws DatatypeException
	 */
	public void setValue(String value) throws DatatypeException {
		this.getScriptParameter().getDatatype().validate(value);
		mValue = value;
	}

}
