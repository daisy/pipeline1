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
import java.util.ArrayList;
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
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.ScriptAbortException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.exception.TransformerAbortException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.mime.MIMETypeRegistry;
import org.daisy.util.mime.MIMETypeRegistryException;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.validation.ValidationException;
import org.daisy.util.xml.validation.Validator;
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
    private int currentTaskIndex = -1;
	
	private Map transformerHandlers;
	
	/**
	 * Creates a Script handler.
	 * @param script a script file
	 * @param evListeners a set of event listeners
	 * @param validator a validator
	 * @throws ScriptException if the script is invalid
	 * @throws MIMEException
	 */
	public ScriptHandler(File script, Map handlers, Set evListeners, Validator validator) throws ScriptException, MIMEException {
		super(evListeners);
		transformerHandlers = handlers;
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setValidating(false);
		
		try {
			// Validate the script file
			if (!validator.isValid(script)) {
				throw new ScriptException(i18n("SCRIPT_NOT_VALID"));
			}

			// Parse the script file
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(script);
						
			// Get properties from script file
			readProperties(doc.getDocumentElement());
				
			/*
			 * More script validation 
			 */
			
			// Make sure every Transformer in the script file exists
			for (Iterator it = tasks.iterator(); it.hasNext(); ) {
				Task task = (Task)it.next();
				TransformerHandler handler = (TransformerHandler)transformerHandlers.get(task.getName());
								
				if (handler == null) {
				    throw new ScriptException(i18n("TRANSFORMER_NOT_KNOWN", task.getName()));
				}
			}

			// Make sure there are no two elements with the same ID.
			// FIXME Use a schematron rule (or similar) instead
			NodeList ids = XPathUtils.selectNodes(doc, "//parameter/@id");
			Set idSet = new HashSet();
			for (int i = 0; i < ids.getLength(); ++i) {
			    String id = XPathUtils.valueOf(ids.item(i), ".");
			    if (!idSet.add(id)) {
			        throw new ScriptException(i18n("ID_ATTRIBUTES_MUST_BE_UNIQUE"));
			    }
			}
						
			// Validate parameters
			for (Iterator it = tasks.iterator(); it.hasNext(); ) {
				Task task = (Task)it.next();
				TransformerHandler handler = (TransformerHandler)transformerHandlers.get(task.getName());
				
				for (Iterator paramIter = task.getParameters().values().iterator(); paramIter.hasNext(); ) {
					Parameter parameter = (Parameter)paramIter.next();					
					if (parameter.getRef() != null) {
					    Node refd = XPathUtils.selectSingleNode(doc, "//task/parameter[@id='" + parameter.getRef() + "']");
																		
						if (refd == null) {
						    throw new ScriptException(i18n("PARAMETER_REFERENCES_WRONG_ID", parameter.getName(), task.getName()));
						}
						
						String taskWithIDName = XPathUtils.valueOf(doc, "//task[parameter/@id='" + parameter.getRef() + "']/@name");
						
						// We already know this handler exists
						TransformerHandler handlerWithID = (TransformerHandler)transformerHandlers.get(taskWithIDName);
						
						// Make sure all in params with a ref has a matching (mime-wise) out param id
						String typeOfRef = handler.getParameterType(parameter.getName());
						String typeOfId = handlerWithID.getParameterType(XPathUtils.valueOf(refd, "name"));						
						//System.err.println("*** typeOfRef: " + _typeOfRef + ", typeOfId: " + _typeOfId);
						//MIMERegistry mime = MIMERegistry.instance();
						MIMETypeRegistry registry = MIMETypeRegistry.getInstance();
						MIMEType typeId = registry.getEntryByName(typeOfId);
						MIMEType typeRef = registry.getEntryByName(typeOfRef);
						if (!typeId.isEqualOrAlias(typeRef)) {
						//if (!mime.matches(typeOfId, typeOfRef)) {
						    Object[] args = {parameter.getRef(), typeOfId, handlerWithID.getName(), typeOfRef, handler.getName()};
						    throw new ScriptException(i18n("MIME_TYPE_MISMATCH", args));
						}
					}
				}				
				
				// Make sure parameters are OK (none missing, no extra etc)
				handler.validateParameters(task.getParameters());
				
			}				
			
		} catch (ValidationException e) {		    
			throw new ScriptException(i18n("SCRIPT_NOT_VALID"), e);
		} catch (ParserConfigurationException e) {
		    throw new ScriptException(i18n("SCRIPT_PARSE_ERROR"), e);
        } catch (SAXException e) {
            throw new ScriptException(i18n("SCRIPT_PARSE_ERROR"), e);
        } catch (IOException e) {
            throw new ScriptException(i18n("SCRIPT_ACCESS_ERROR") , e);
        } catch (MIMETypeException e) {
        	throw new ScriptException(i18n("MIME_TYPE_MISMATCH") , e);
		} catch (MIMETypeRegistryException e) {
			throw new ScriptException(i18n("MIME_TYPE_MISMATCH") , e);
		} 	
	}
	
	/**
	 * Reads the properties in the script file.
	 * @param element
	 */
	private void readProperties(Element element) {
	    name = XPathUtils.valueOf(element, "name");
	    description = XPathUtils.valueOf(element, "description");
	    version = XPathUtils.valueOf(element, "@version");
	    
	    // Read properties
	    NodeList propertyList = XPathUtils.selectNodes(element, "property");
	    for (int i = 0; i < propertyList.getLength(); ++i) {
	        Element property = (Element)propertyList.item(i);
	        String propertyName = XPathUtils.valueOf(property, "@name");
	        String value = XPathUtils.valueOf(property, "@value");
	        String type = XPathUtils.valueOf(property, "@type");
	        Property prop = new Property(propertyName, value, type);
	        properties.put(propertyName, prop);
	    }
	    
	    // Read tasks
	    NodeList taskList = XPathUtils.selectNodes(element, "task");
	    for (int i = 0; i < taskList.getLength(); ++i) {
	        Element taskElement = (Element)taskList.item(i);
	        Task task = new Task(taskElement, properties);
	        tasks.add(task);
	    }
	}
	
	/**
	 * Iterates over all tasks and executes each and every one of them
	 */
	public void execute() throws ScriptException {
	    sendMessage(Level.CONFIG, i18n("RUNNING_SCRIPT", name));
		
        currentTaskIndex = -1;
        
		try {
			for (Iterator it = tasks.iterator(); it.hasNext(); ) {
				Task task = (Task)it.next();
				TransformerHandler th = (TransformerHandler)transformerHandlers.get(task.getName());
				sendMessage(Level.CONFIG, i18n("RUNNING_TASK", th.getName()));
                currentTaskIndex++;
				if (!th.run(task.getParameters(), task.isInteractive())) {
				    throw new ScriptException(i18n("TASK_FAILED", th.getName()));
				}
			}
		} catch (TransformerAbortException e) {
		    throw new ScriptAbortException("Script aborted.");
		} catch (TransformerRunException e) {
		    e.printStackTrace();
		    throw new ScriptException(i18n("ERROR_RUNNING_TASK"), e);
		}
		
		sendMessage(Level.CONFIG, i18n("END_OF_SCRIPT"));
	}
    
    public boolean setProperty(String name, String value) {
        boolean ret = properties.containsKey(name);
        if (ret) {
        	Property prop = (Property)properties.get(name);
        	prop.setValue(value);
        } else {
        	properties.put(name, new Property(name, value));
        }        
        return ret;
    }

    /**
     * Get the description of a script
     * @return a description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the name of a script
     * @return the name of the script
     */
    public String getName() {
        return name;
    }

    /**
     * Get all properties of a script
     * @return a Map of properties
     */
    public Map getProperties() {
        return properties;
    }

    public List getTasks() {
        return tasks;
    }
    
    public int getTaskCount() {
        return tasks.size();
    }
    
    public List getTransformerInfoList() {
        List transformerInfoList = new ArrayList(this.getTaskCount());
        for (Iterator it = tasks.iterator(); it.hasNext(); ) {
            Task task = (Task)it.next();
            String name = task.getName();
            TransformerHandler th = (TransformerHandler)transformerHandlers.get(name);
            transformerInfoList.add(th);
        }
        return transformerInfoList;
    }
    
    public int getCurrentTaskIndex() {
        return currentTaskIndex;
    }
	
}
