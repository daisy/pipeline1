/*
 * Created on 2005-mar-18
 */
package org.daisy.dmfc.core.script;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;

/**
 * @author LINUSE
 */
public class Task {
	
	private String name;
	private boolean interactive;
	private Collection parameters = new Vector();
	
	public Task(Element a_element) {
		name = a_element.valueOf("@name");
		System.err.println("task " + name);
		interactive = Boolean.getBoolean(a_element.valueOf("@interactive"));
		
		XPath _xpathSelector = DocumentHelper.createXPath("parameter");
		List _parameters = _xpathSelector.selectNodes(a_element);
		for (Iterator _iter = _parameters.iterator(); _iter.hasNext(); ) {
			Element _parameter = (Element)_iter.next();
			Parameter _param = new Parameter(_parameter);
			parameters.add(_param);
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
	public Collection getParameters() {
		return parameters;
	}

}
