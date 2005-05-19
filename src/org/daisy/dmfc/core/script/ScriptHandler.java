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
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.dmfc.core.EventSender;
import org.daisy.dmfc.core.MIMERegistry;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.exception.ValidationException;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.validator.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


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
	public ScriptHandler(File a_script, Map a_transformerHandlers, Set a_eventListeners, Validator a_validator) throws ScriptException, MIMEException {
		super(a_eventListeners);
		transformerHandlers = a_transformerHandlers;
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setValidating(false);
		
		try {
			// Validate the script file
			if (!a_validator.isValid(a_script)) {
				throw new ScriptException("Script file is not valid");
			}
			
			// Parse the script file
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(a_script);
						
			// Get properties from script file
			readProperties(doc.getDocumentElement());
			
			// Add useful default properties
			properties.put("dollar", "\\$");
				
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
			NodeList ids = XPathUtils.selectNodes(doc, "//parameter/@id");
			Set idSet = new HashSet();
			for (int i = 0; i < ids.getLength(); ++i) {
			    String id = XPathUtils.valueOf(ids.item(i), ".");
			    if (!idSet.add(id)) {
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
					    Node refd = XPathUtils.selectSingleNode(doc, "//task/parameter[@id='" + _parameter.getRef() + "']");
																		
						if (refd == null) {
							throw new ScriptException("Parameter " + _parameter.getName() + " in task " + 
									_task.getName() + " has a reference to a non-existing id");
						}
						
						String taskWithIDName = XPathUtils.valueOf(doc, "//task[parameter/@id='" + _parameter.getRef() + "']/@name");
						
						// We already know this handler exists
						TransformerHandler _handlerWithID = (TransformerHandler)transformerHandlers.get(taskWithIDName);
						
						// Make sure all in params with a ref has a matching (mime-wise) out param id
						String _typeOfRef = _handler.getParameterType(_parameter.getName());
						String _typeOfId = _handlerWithID.getParameterType(XPathUtils.valueOf(refd, "name"));						
						//System.err.println("*** typeOfRef: " + _typeOfRef + ", typeOfId: " + _typeOfId);
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
		} catch (ParserConfigurationException e) {
		    throw new ScriptException("Problems parsing script file" , e);
        } catch (SAXException e) {
            throw new ScriptException("Problems parsing script file" , e);
        } catch (IOException e) {
            throw new ScriptException("Problems accessing script file" , e);
        }		
	}
	
	/**
	 * Reads the properties in the script file.
	 * @param a_element
	 */
	private void readProperties(Element a_element) {
	    name = XPathUtils.valueOf(a_element, "name");
	    description = XPathUtils.valueOf(a_element, "description");
	    version = XPathUtils.valueOf(a_element, "@version");
	    
	    // Read properties
	    NodeList propertyList = XPathUtils.selectNodes(a_element, "property");
	    for (int i = 0; i < propertyList.getLength(); ++i) {
	        Element property = (Element)propertyList.item(i);
	        String propertyName = XPathUtils.valueOf(property, "@name");
	        String value = XPathUtils.valueOf(property, "@value");
	        properties.put(propertyName, value);
	    }
	    
	    // Read tasks
	    NodeList taskList = XPathUtils.selectNodes(a_element, "task");
	    for (int i = 0; i < taskList.getLength(); ++i) {
	        Element taskElement = (Element)taskList.item(i);
	        Task task = new Task(taskElement, properties);
	        tasks.add(task);
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
				sendMessage(Level.CONFIG, i18n("RUNNING_TASK", _task.getName()));
				if (!_th.run(_task.getParameters(), _task.isInteractive())) {
				    throw new ScriptException("Task " + _task.getName() + " failed");
				}
			}
		} catch (TransformerRunException e) {
			throw new ScriptException("Error while running task", e);
		}
		
		sendMessage(Level.CONFIG, "End of running script");
	}
	
}
