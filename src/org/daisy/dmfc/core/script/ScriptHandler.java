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

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.dmfc.core.EventSender;
import org.daisy.dmfc.core.MIMERegistry;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.exception.ValidationException;
import org.daisy.util.i18n.I18n;
import org.daisy.util.xml.validator.Validator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

/**
 * This class loads and executes the script files.
 * @author Linus Ericson
 */
public class ScriptHandler extends EventSender {

	private String name;
	private String description;
	private Map properties = new HashMap();
	private String version;
	private List tasks = new LinkedList();
	
	private Map transformerHandlers;
	
	/**
	 * Creates a Script handler.
	 * @param a_script a script file
	 * @param a_eventListeners a set of event listeners
	 * @param a_validator a validator
	 * @throws ScriptException if the script is invalid
	 * @throws MIMEException
	 */
	public ScriptHandler(File a_script, Map a_transformerHandlers, I18n a_i18n, Set a_eventListeners, Validator a_validator) throws ScriptException, MIMEException {
		super(a_eventListeners);
		transformerHandlers = a_transformerHandlers;
		setI18n(a_i18n);
		
		try {
			// Validate the script file
			if (!a_validator.isValid(a_script)) {
				throw new ScriptException("Script file is not valid");
			}
			
			// Parse the script file into a dom4j Document
			SAXReader _xmlReader = new SAXReader();
			Document _doc = _xmlReader.read(a_script);
			
			// Get properties from script file
			readProperties(_doc.getRootElement());
				
			/*
			 * More script validation 
			 */
			
			// Make sure every Transformer in the script file exists
			for (Iterator _iter = tasks.iterator(); _iter.hasNext(); ) {
				Task _task = (Task)_iter.next();
				TransformerHandler _handler = (TransformerHandler)transformerHandlers.get(_task.getName());
								
				if (_handler == null) {
					throw new ScriptException("Transformer " + _task.getName() + " is not a known Transformer");
				}
			}

			// Make sure there are no two elements with the same ID.
			// FIXME Use a schematron rule (or similar) instead
			XPath _xpathSelector = DocumentHelper.createXPath("//parameter/@id");
			List _ids = _xpathSelector.selectNodes(_doc);
			Set _idSet = new HashSet();
			for (Iterator _iter = _ids.iterator(); _iter.hasNext(); ) {
				String _id = ((Node)_iter.next()).valueOf(".");
				if(!_idSet.add(_id)) {
					throw new ScriptException("id attributes must be unique");
				}
			}
			
			// Validate parameters
			for (Iterator _iter = tasks.iterator(); _iter.hasNext(); ) {
				Task _task = (Task)_iter.next();
				TransformerHandler _handler = (TransformerHandler)transformerHandlers.get(_task.getName());
				
				for (Iterator _paramIter = _task.getParameters().values().iterator(); _paramIter.hasNext(); ) {
					Parameter _parameter = (Parameter)_paramIter.next();					
					if (_parameter.getRef() != null) {
						Node _refd = _doc.selectSingleNode("//task/parameter[@id='" + _parameter.getRef() + "']");
												
						if (_refd == null) {
							throw new ScriptException("Parameter " + _parameter.getName() + " in task " + 
									_task.getName() + " has a reference to a non-existing id");
						}
						
						String _taskWithIDName = _doc.valueOf("//task[parameter/@id='" + _parameter.getRef() + "']/@name");
						// We already know this handler exists
						TransformerHandler _handlerWithID = (TransformerHandler)transformerHandlers.get(_taskWithIDName);
						
						// Make sure all in params with a ref has a matching (mime-wise) out param id
						String _typeOfRef = _handler.getParameterType(_parameter.getName());
						String _typeOfId = _handlerWithID.getParameterType(_refd.valueOf("name"));
						System.err.println("*** typeOfRef: " + _typeOfRef + ", typeOfId: " + _typeOfId);
						MIMERegistry _mime = MIMERegistry.instance();
						if (!_mime.matches(_typeOfId, _typeOfRef)) {
							throw new ScriptException("Where id/ref = " + _parameter.getRef() + ": MIME type '" + 
									_typeOfId + "' of Transformer '" + _handlerWithID.getName() +
									"' is not compatible with MIME type '" + _typeOfRef + "' of Transformer '" + 
									_handler.getName() + "'");
						}
					}
				}				
				
				// Make sure parameters are OK (none missing, no extra etc)
				_handler.validateParameters(_task.getParameters());
				
			}				
			
		} catch (ValidationException e) {
			throw new ScriptException("Script file is not valid", e);
		} catch (DocumentException e) {
			throw new ScriptException("Problems parsing script file" , e);
		}
	}
	
	/**
	 * Reads the properties in the script file.
	 * @param a_element
	 */
	private void readProperties(Element a_element) {
		this.name = a_element.valueOf("name");
		this.description = a_element.valueOf("description");
		this.version = a_element.valueOf("@version");
		
		// Read properties
		XPath _xpathSelector = DocumentHelper.createXPath("property");
		List _properties = _xpathSelector.selectNodes(a_element);
		for (Iterator _iter = _properties.iterator(); _iter.hasNext(); ) {
			Element _property = (Element)_iter.next();
			String _name = _property.valueOf("@name");
			String _value = _property.valueOf("@value");
			properties.put(_name, _value);
		}
		
		// Read tasks
		_xpathSelector = DocumentHelper.createXPath("task");
		List _tasks = _xpathSelector.selectNodes(a_element);
		for (Iterator _iter = _tasks.iterator(); _iter.hasNext(); ) {
			Element _taskElement = (Element)_iter.next();
			Task _task = new Task(_taskElement, properties);			
			tasks.add(_task);
		}
	}
	
	/**
	 * Iterates over all tasks and executes each ane every one of them
	 */
	public void execute() throws ScriptException {
	    sendMessage(Level.CONFIG, i18n("RUNNING_SCRIPT", name));
		
		try {
			for (Iterator _iter = tasks.iterator(); _iter.hasNext(); ) {
				Task _task = (Task)_iter.next();
				TransformerHandler _th = (TransformerHandler)transformerHandlers.get(_task.getName());
				sendMessage(Level.CONFIG, "Running task: '" + _task.getName() + "'");
				_th.run(_task.getParameters(), _task.isInteractive());				
			}
		} catch (TransformerRunException e) {
			throw new ScriptException("Error while running task", e);
		}
		
		sendMessage(Level.CONFIG, "End of running script");
	}
	
}
