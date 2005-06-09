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
package org.daisy.dmfc.core.transformer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.daisy.dmfc.core.DirClassLoader;
import org.daisy.dmfc.core.EventSender;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.TransformerDisabledException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.xml.XPathUtils;
import org.daisy.util.xml.validation.ValidationException;
import org.daisy.util.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Handles descriptions of and initiates the execution of Transformers.
 * The TransformerHandler class is responsible for parsing the Transformer description
 * file (TDF), finding the Java class associated with the Transformer and loading it.
 * It is also responsible for verifying that the parameters sent from the execution
 * script matches the parameters described in the TDF.
 *   
 * @author Linus Ericson
 */
public class TransformerHandler extends EventSender implements TransformerInfo {

	private String name;
	private String description;
	private String classname;
	private Set jars = new HashSet();
	private String version;
	private Vector parameters = new Vector();
	
	private InputListener inputListener;
		
	private DirClassLoader transformerClassLoader;
	private Class transformerClass;
	private Constructor transformerConstructor;
	
	/**
	 * Creates a Transformer handler.
	 * @param transformerDescription a transformer description file
	 * @param inListener an input listener
	 * @param eventListeners an event listener
	 * @throws TransformerDisabledException
	 */
	public TransformerHandler(File transformerDescription, InputListener inListener, Set eventListeners, Validator validator) throws TransformerDisabledException {
		super(eventListeners);
		inputListener = inListener;		
				
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setValidating(false);
		
		/*
		 * If any validation or dependency check fails, disable this Transformer 
		 */
		try {						
			// Validate the transformer description file
			if (!validator.isValid(transformerDescription)) {
			    throw new TransformerDisabledException(i18n("TDF_NOT_VALID"));
			}
			
			// Parse the description file
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(transformerDescription);
					
			// Get properties from transformer description file
			readProperties(doc.getDocumentElement(), transformerDescription.getParentFile());
						
			// Perform platform dependency checks
			if (!isPlatformOk(doc.getDocumentElement())) {
			    throw new TransformerDisabledException(i18n("PLATFORM_CHECK_FAILED"));
			}
			
			// Create the class loader and Transformer class (not object)
			createTransformerClass(transformerDescription);
			
			// Make sure the right constructor is present
			checkForTransformerConstructor();
			
			// Do dependency checks in the class associated with the Transformer
			if (!transformerSupported()) {
			    throw new TransformerDisabledException(i18n("TRANSFORMER_NOT_SUPPORTED"));
			}
		} catch (ClassNotFoundException e) {
			throw new TransformerDisabledException(i18n("CANNOT_CREATE_TRANSFORMER_CLASS"), e);
		} catch (NoSuchMethodException e) {
		    throw new TransformerDisabledException(i18n("NOSUCHMETHOD_IN_TRANSFORMER"), e);
		} catch (TransformerRunException e) {
			throw new TransformerDisabledException("Cannot run static isSupported method of Transformer", e);
		} catch (ValidationException e) {
			throw new TransformerDisabledException(i18n("TDF_VALIDATION_EXCEPTION"), e);
		} catch (MIMEException e) {
		    throw new TransformerDisabledException("MIME exception", e);
        } catch (ParserConfigurationException e) {
            throw new TransformerDisabledException(i18n("PROBLEMS_PARSING_TDF"), e);
        } catch (SAXException e) {
            throw new TransformerDisabledException(i18n("PROBLEMS_PARSING_TDF"), e);
        } catch (IOException e) {
            throw new TransformerDisabledException(i18n("TDF_IO_EXCEPTION"), e);
        }	
	}
	
	
	/**
	 * Run the Transformer associated with this handler.
	 * @param runParameters parameters to the Transformer
	 * @return <code>true</code> if the run was successful, <code>false</code> otherwise
	 */
	public boolean run(Map runParameters, boolean interactive) throws TransformerRunException {
		Transformer transformer = null;
		try {
			transformer = createTransformerObject(interactive);
		} catch (IllegalArgumentException e) {
		    throw new TransformerRunException(i18n("TRANSFORMER_ILLEGAL_ARGUMENT"), e);
		} catch (InstantiationException e) {
			throw new TransformerRunException("Instantiation problems", e);
		} catch (IllegalAccessException e) {
		    throw new TransformerRunException(i18n("TRANSFORMER_ILLEGAL_ACCESS"), e);
		} catch (InvocationTargetException e) {
		    throw new TransformerRunException(i18n("TRANSFORMER_INVOCATION_PROBLEM"), e);
		}		
		
		// Turn the parameters to a simple key->value string map
		Map params = new LinkedHashMap();
		for (Iterator it = runParameters.entrySet().iterator(); it.hasNext(); ) {
		    Map.Entry entry = (Map.Entry)it.next();
		    params.put(entry.getKey(), ((org.daisy.dmfc.core.script.Parameter)entry.getValue()).getValue());
		}
				
		return transformer.executeWrapper(params);
	}
	
	/**
	 * Checks if the parameters in a task script are valid for this Transformer.
	 * Also add any hard-coded parameters. 
	 * @param params a collection of parameters
	 */
	public void validateParameters(Map params) throws ValidationException {
		HashMap map = new HashMap();

		// Add all hard-coded parameters in the TDF to the script parameters
		for (Iterator it = parameters.iterator(); it.hasNext(); ) {
		    Parameter param = (Parameter)it.next();
			if (param.getValue() != null) {
			    if (params.containsKey(param.getName())) {			        
			        throw new ValidationException(i18n("PARAM_NOT_BY_USER", param.getName()));
			    }
			    org.daisy.dmfc.core.script.Parameter scriptParameter = new org.daisy.dmfc.core.script.Parameter(param.getName(), param.getValue()); 
			    params.put(param.getName(), scriptParameter);
			}
		}
		
		// Make sure there are no parameters in the script file that is not in the TDF.
		for (Iterator it = parameters.iterator(); it.hasNext(); ) {
			Parameter transformerParam = (Parameter)it.next();			
			map.put(transformerParam.getName(), transformerParam);
		}		
		for (Iterator it = params.values().iterator(); it.hasNext(); ) {
			org.daisy.dmfc.core.script.Parameter scriptParameter = (org.daisy.dmfc.core.script.Parameter)it.next();
			Parameter transformerParameter = (Parameter)map.get(scriptParameter.getName());
			if (transformerParameter == null) {
			    throw new ValidationException(i18n("PARAMETER_NOT_RECOGNIZED", scriptParameter.getName(), getName()));
			}
		}		
		
		// Make sure there are no required parameters in the TDF that are not present in the script file
		for (Iterator it = parameters.iterator(); it.hasNext(); ) {
			Parameter transformerParam = (Parameter)it.next();
			if (transformerParam.isRequired()) {
				org.daisy.dmfc.core.script.Parameter scriptParam = (org.daisy.dmfc.core.script.Parameter)params.get(transformerParam.getName());
				if (scriptParam == null) {
				    throw new ValidationException(i18n("PARAMETER_REQUIRED", transformerParam.getName(), getName()));
				}
			} else {
			    org.daisy.dmfc.core.script.Parameter scriptParam = (org.daisy.dmfc.core.script.Parameter)params.get(transformerParam.getName());
				if (scriptParam == null) {
					params.put(transformerParam.getName(), new org.daisy.dmfc.core.script.Parameter(transformerParam.getName(), transformerParam.getDefaultValue()));
				}
			}
		}
	}
	
	/**
	 * Tries to find and load the Java class associated with this Transformer.
	 * @param transformerDescription
	 * @throws ClassNotFoundException
	 */
	private void createTransformerClass(File transformerDescription) throws ClassNotFoundException {
	    sendMessage(Level.FINE, i18n("LOADING_TRANSFORMER", name, classname));
		File dir = transformerDescription.getAbsoluteFile();
		dir = dir.getParentFile();
		transformerClassLoader = new DirClassLoader(new File(System.getProperty("dmfc.home"), "transformers"), dir);
		for (Iterator it = jars.iterator(); it.hasNext(); ) {
		    String jar = (String)it.next();
		    transformerClassLoader.addJar(new File(dir, jar));
		}		
		transformerClass = Class.forName(classname, true, transformerClassLoader);
	}
	
	/**
	 * Creates an instance object of the Transformer class.
	 * @param interactive
	 * @return a <code>Transformer</code> object
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Transformer createTransformerObject(boolean interactive) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {		
		Object params[] = {inputListener, this.getEventListeners(), Boolean.valueOf(interactive)};
		Transformer trans = (Transformer)transformerConstructor.newInstance(params);
		trans.setMessageOriginator(name);
		return trans;
	}
	
	/**
	 * Reads the properties in the TDF.
	 * @param element
	 * @throws MIMEException
	 */
	private void readProperties(Element element, File tdfDir) throws MIMEException {
	    name = XPathUtils.valueOf(element, "name");
	    description = XPathUtils.valueOf(element, "description");
	    classname = XPathUtils.valueOf(element, "classname");
		
	    NodeList jarList = XPathUtils.selectNodes(element, "jar");
	    for (int i = 0; i < jarList.getLength(); ++i) {
	        Element jar = (Element)jarList.item(i);
	        jars.add(XPathUtils.valueOf(jar, "."));
	    }
	    
	    version = XPathUtils.valueOf(element, "@version");	    		
		
	    NodeList parameterList = XPathUtils.selectNodes(element, "parameters/parameter");
	    for (int i = 0; i < parameterList.getLength(); ++i) {
	        Element parameter = (Element)parameterList.item(i);
	        Parameter param = new Parameter(parameter, tdfDir);
	        parameters.add(param);
	    }	    
	}
	
	/**
	 * Makes sure the constructor we wish to use exists.
	 * @throws NoSuchMethodException
	 */
	private void checkForTransformerConstructor() throws NoSuchMethodException {
		Class[] params = {InputListener.class, Set.class, Boolean.class};
		transformerConstructor = transformerClass.getConstructor(params);
	}
	
	/**
	 * Calls the static method <code>isSupported</code> in the Transformer class
	 * and returns the result.
	 * @return
	 * @throws NoSuchMethodException
	 * @throws TransformerRunException
	 */
	private boolean transformerSupported() throws NoSuchMethodException, TransformerRunException {
		Method isSupportedMethod = transformerClass.getMethod("isSupported", null);
		Boolean result;
		try {
			result = (Boolean)isSupportedMethod.invoke(null, null);
		} catch (IllegalArgumentException e) {
			throw new TransformerRunException(i18n("TRANSFORMER_ILLEGAL_ARGUMENT"), e);
		} catch (IllegalAccessException e) {
			throw new TransformerRunException(i18n("TRANSFORMER_ILLEGAL_ACCESS"), e);
		} catch (InvocationTargetException e) {
			throw new TransformerRunException(i18n("TRANSFORMER_INVOCATION_PROBLEM"), e);
		}
		return result.booleanValue();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Gets a collection of the parameters of the Transformer. Only the
	 * parameters that are of interest to a user of a Transformer is
	 * returned. Any hard coded parameters that cannot be changed
	 * will not be returned.
	 */
	public Collection getParameters() {
	    Vector params = (Vector)parameters.clone();	    
	    // Remove all hard coded parameters
	    for (int i = 0; i < parameters.size(); ++i) {
	        ParameterInfo param = (ParameterInfo)parameters.elementAt(i);
	        if (param.getValue() != null) {
	            //System.err.println("Removing hard coded param " + _param.getName());
	            params.remove(param);
	        }
	    }
		return params;
	}	
	
	/**
	 * Checks if the current platform is supported by this Transformer
	 * @param element
	 * @return <code>true</code> if the platform is supported, <code>false</code> otherwise
	 */
	private boolean isPlatformOk(Element element) {
		boolean ret = true;
		NodeList platformList = XPathUtils.selectNodes(element, "platforms/platform");
		if (platformList.getLength() > 0) {
		    ret = false;
		}
		for (int i = 0; i < platformList.getLength(); ++i) {
		    Element platform = (Element)platformList.item(i);			
			boolean platformOk = true;
			NodeList propertyList = XPathUtils.selectNodes(platform, "property");
			for (int j = 0; j < propertyList.getLength(); ++j) {		
			    Element property = (Element)propertyList.item(j);
			    String propertyName = XPathUtils.valueOf(property, "name");
			    String value = XPathUtils.valueOf(property, "value");				
				String realValue = System.getProperty(propertyName);
				if (realValue == null) {
					platformOk = false;
					sendMessage(Level.WARNING, i18n("UNKNOWN_PROPERTY", propertyName));
				}
				else {
					if (!realValue.matches(value)) {
						platformOk = false;
					}
					//System.err.println("Property: " + propertyName + ", value: " + value + ", real: " + real);
				}
			}
			if (platformOk) {
				ret = true;
			}
		}
		return ret;
	}
	
	public String getParameterType(String parameterName) {
		for (Iterator it = parameters.iterator(); it.hasNext(); ) {
			Parameter param = (Parameter)it.next();
			if (parameterName.equals(param.getName())) {
				return param.getType();
			}
		}
		return null;
	}
}
