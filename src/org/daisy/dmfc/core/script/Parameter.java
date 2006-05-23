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

import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Element;

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
    
    private Map propertiesRef;
	
	/**
	 * Constructor
	 * @param element
	 */
	public Parameter(Element element, Map properties) {
	    name = XPathUtils.valueOf(element, "name");
	    value = XPathUtils.valueOf(element, "value");
        propertiesRef = properties;
		
				
		id = XPathUtils.valueOf(element, "@id");
		if (XPathUtils.selectSingleNode(element, "@ref") != null) {
		    ref = XPathUtils.valueOf(element, "@ref");
		}		
	}
	
	public Parameter(String paramName, String paramValue) {
	    name = paramName;
	    value = paramValue;
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
        if (value == null) {
            return null;
        }
        if (propertiesRef == null) {
        	return value;
        }
	    // Expand properties in the value string     
        Matcher matcher = propertyPattern.matcher(value);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String propName = matcher.group(1);
            String prop = (String)((Property)propertiesRef.get(propName)).getValue();
            matcher.appendReplacement(sb, prop.replaceAll("\\\\", "\\\\\\\\"));
        }
        matcher.appendTail(sb);
        return sb.toString();
	}

}
