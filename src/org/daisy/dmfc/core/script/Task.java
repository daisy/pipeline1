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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

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
	 * @param a_element the element to create the <code>Task</code> from
	 * @param a_properties any properties that have been set in the script file
	 */
	public Task(Element a_element, Map a_properties) {
		name = a_element.valueOf("@name");
		System.err.println("task " + name);
		interactive = Boolean.getBoolean(a_element.valueOf("@interactive"));
		
		XPath _xpathSelector = DocumentHelper.createXPath("parameter");
		List _parameters = _xpathSelector.selectNodes(a_element);
		for (Iterator _iter = _parameters.iterator(); _iter.hasNext(); ) {
			Element _parameter = (Element)_iter.next();
			Parameter _param = new Parameter(_parameter, a_properties);
			parameters.put(_param.getName(), _param);			
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
