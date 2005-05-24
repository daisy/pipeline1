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

import java.util.LinkedHashMap;
import java.util.Map;

import org.daisy.util.xml.XPathUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A task in a script file.
 * @author Linus Ericson
 */
public class Task {
	
	private String name;
	private boolean interactive;
	private Map parameters = new LinkedHashMap();
	
	/**
	 * Creates a new <code>Task</code>
	 * @param element the element to create the <code>Task</code> from
	 * @param properties any properties that have been set in the script file
	 */
	public Task(Element element, Map properties) {
	    name = XPathUtils.valueOf(element, "@name");		
		interactive = Boolean.valueOf(XPathUtils.valueOf(element, "@interactive")).booleanValue();
		
		NodeList nodeList = XPathUtils.selectNodes(element, "parameter");
		for (int i = 0; i < nodeList.getLength(); ++i) {
		    Element parameter = (Element)nodeList.item(i);
		    Parameter param = new Parameter(parameter, properties);
		    parameters.put(param.getName(), param);
		}		
	}
	/**
	 * @return Returns the interactive.
	 */
	public boolean isInteractive() {
		return interactive;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the parameters.
	 */
	public Map getParameters() {
		return parameters;
	}

}
