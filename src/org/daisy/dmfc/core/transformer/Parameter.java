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
package org.daisy.dmfc.core.transformer;

import org.dom4j.Element;

/**
 * A parameter in the Transformer Description File (TDF).
 * @author Linus Ericson
 */
public class Parameter {
	
	private String name;
	private String description;
	private String example;
	private boolean required;
	private String direction;
	private String type;
	private String defaultValue;
	private String value = null;
	
	public Parameter(Element a_parameter) {
		name = a_parameter.valueOf("name");
		description = a_parameter.valueOf("description");
		example = a_parameter.valueOf("example");
		required = Boolean.valueOf(a_parameter.valueOf("@required")).booleanValue();
		direction = a_parameter.valueOf("@direction");
		// FIXME make sure the type matches a valid mime-type
		type = a_parameter.valueOf("@type");
		defaultValue = a_parameter.valueOf("default");
		
		if (a_parameter.selectSingleNode("value") != null) {
		    value = a_parameter.valueOf("value");
		}
	}
	
	/**
	 * @return Returns the direction.
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * @return Returns the example.
	 */
	public String getExample() {
		return example;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the required.
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @return Returns the type.
	 */
	public String getType() {
		return type;
	}

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }
    
    /**
     * @return Returns the defaultValue.
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}
