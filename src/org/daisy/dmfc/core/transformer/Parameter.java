/*
 * Created on 2005-mar-07
 */
package org.daisy.dmfc.core.transformer;

import org.dom4j.Element;

/**
 * A parameter in the Transformer Description File (TDF).
 * @author LINUSE
 */
public class Parameter {
	
	private String name;
	private String description;
	private String example;
	private boolean required;
	private String direction;
	private String type;
	private String defaultValue;
	
	public Parameter(Element a_parameter) {
		name = a_parameter.valueOf("name");
		description = a_parameter.valueOf("description");
		example = a_parameter.valueOf("example");
		required = Boolean.valueOf(a_parameter.valueOf("@required")).booleanValue();
		direction = a_parameter.valueOf("@direction");
		// FIXME make sure the type matches a valid mime-type
		type = a_parameter.valueOf("@type");
		defaultValue = a_parameter.valueOf("default");
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

}
