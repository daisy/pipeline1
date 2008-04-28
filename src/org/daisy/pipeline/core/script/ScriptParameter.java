/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.core.script;

import java.util.Map;

import org.daisy.pipeline.core.script.datatype.Datatype;

/**
 * A class representing a script parameter.
 * @author Linus Ericson
 */
public class ScriptParameter extends AbstractProperty {
	
	private boolean required;
	private String nicename;
	private String description;
	private Datatype datatype;
	
	/**
	 * Constructor.
	 * @param name the name of the parameter
	 * @param value the value of the parameter
	 * @param properties a Map of properties
	 * @param required
	 * @throws ScriptValidationException 
	 */
	ScriptParameter(String name, 
            	   String value, 
            	   Map<String, AbstractProperty> properties, 
            	   boolean required) throws ScriptValidationException {
		super(name, value, properties);
		this.required = required;
	}

	/**
	 * Should the user be required to specify a value for this parameter when the
	 * script is run?
	 * @return true if this parameter is required, false otherwise
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Gets the Datatype for this parameter
	 * @return the Datatype of this parameter
	 */
	public Datatype getDatatype() {
		return datatype;
	}

	/**
	 * Gets the description of this parameter
	 * @return a description of the parameter
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the nicename of this parameter
	 * @return the nicename of the parameter
	 */
	public String getNicename() {
		return nicename;
	}

	/**
	 * Sets the datatype for this parameter
	 * @param datatype
	 */
	void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	/**
	 * Sets the description of this parameter
	 * @param description
	 */
	void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the nicename of this parameter
	 * @param niceName
	 */
	void setNicename(String niceName) {
		this.nicename = niceName;
	}
	
}
