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
package org.daisy.dmfc.core.script;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;

/**
 * A parameter to a task.
 * This parameter is sent to the transformer when
 * the task is executed.
 * 
 * @author Linus Ericson
 */
public class Parameter {

    private static Pattern propertyPattern = Pattern.compile("\\$\\{(\\w+)\\}");
    
	private String name = null;
	private String value = null;
	private String id = null;
	private String ref = null;
	
	/**
	 * Constructor
	 * @param a_element
	 */
	public Parameter(Element a_element, Map a_properties) {
		name = a_element.valueOf("name");
		System.err.println("param " + name);
		value = a_element.valueOf("value");
		
		// Expand properties in the value string		
		Matcher _matcher = propertyPattern.matcher(value);
		StringBuffer _sb = new StringBuffer();
		while (_matcher.find()) {
		    String _propName = _matcher.group(1);
		    String _prop = (String)a_properties.get(_propName);
		    _matcher.appendReplacement(_sb, _prop);
		}
		_matcher.appendTail(_sb);
		value = _sb.toString();
		
		id = a_element.valueOf("@id");
		if (a_element.selectSingleNode("@ref") != null) {
			ref = a_element.valueOf("@ref");
		}
	}
	
	public Parameter(String a_name, String a_value) {
	    name = a_name;
	    value = a_value;
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
