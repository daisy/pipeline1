/*
 * Created on 2005-mar-18
 */
package org.daisy.dmfc.core.script;

import org.dom4j.Element;

/**
 * A parameter to a task.
 * This parameter is sent to the transformer when
 * the task is executed.
 * 
 * @author LINUSE
 */
public class Parameter {

	private String name = null;
	private String value = null;
	private String id = null;
	private String ref = null;
	
	public Parameter(Element a_element) {
		name = a_element.valueOf("name");
		System.err.println("param " + name);
		value = a_element.valueOf("value");
		id = a_element.valueOf("@id");
		if (a_element.selectSingleNode("@ref") != null) {
			ref = a_element.valueOf("@ref");
		}
		System.err.println("ref set to: '" + (ref==null?"null":ref) + "'");
	}
	
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the ref.
	 */
	public String getRef() {
		return ref;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

}
