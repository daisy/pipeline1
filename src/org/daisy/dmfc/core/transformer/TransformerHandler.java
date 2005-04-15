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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import org.daisy.dmfc.core.DirClassLoader;
import org.daisy.dmfc.core.EventSender;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.TransformerDisabledException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.exception.ValidationException;
import org.daisy.util.xml.validator.Validator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

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
	private String version;
	private Vector parameters = new Vector();
	
	private InputListener inputListener;
		
	private ClassLoader transformerClassLoader;
	private Class transformerClass;
	private Constructor transformerConstructor;
	
	/**
	 * Creates a Transformer handler.
	 * @param a_transformerDescription a transformer description file
	 * @param a_inputListener an input listener
	 * @param a_eventListeners an event listener
	 * @throws TransformerDisabledException
	 */
	public TransformerHandler(File a_transformerDescription, InputListener a_inputListener, Set a_eventListeners, Validator a_validator) throws TransformerDisabledException {
		super(a_eventListeners);
		inputListener = a_inputListener;		
		
		/*
		 * If any validation or dependency check fails, disable this Transformer 
		 */
		try {						
			// Validate the transformer description file
			if (!a_validator.isValid(a_transformerDescription)) {
				throw new TransformerDisabledException("Transformer description file is not valid");
			}
			
			// Parse the description file into a dom4j Document
			SAXReader _xmlReader = new SAXReader();
			Document _doc = _xmlReader.read(a_transformerDescription);
			
			// Get properties from transformer description file
			readProperties(_doc.getRootElement());
			
			// Perform platform dependency checks
			if (!isPlatformOk(_doc.getRootElement())) {
				throw new TransformerDisabledException("Platform dependency check failed");
			}
			
			// Create the class loader and Transformer class (not object)
			createTransformerClass(a_transformerDescription);
			
			// Make sure the right constructor is present
			checkForTransformerConstructor();
			
			// Do dependency checks in the class associated with the Transformer
			if (!transformerSupported()) {
				throw new TransformerDisabledException("Transformer not supported");
			}
		} catch (DocumentException e) {
			throw new TransformerDisabledException("Problems parsing Transformer description file", e);
		} catch (ClassNotFoundException e) {
			throw new TransformerDisabledException("Cannot create class for Transformer", e);
		} catch (NoSuchMethodException e) {
			throw new TransformerDisabledException("Cannot find constructor or method of Transformer", e);
		} catch (TransformerRunException e) {
			throw new TransformerDisabledException("Cannot run static isSupported method of Transformer", e);
		} catch (ValidationException e) {
			throw new TransformerDisabledException(i18n("TDF_VALIDATION_EXCEPTION"), e);
		} catch (MIMEException e) {
		    throw new TransformerDisabledException("MIME exception", e);
        }	
	}
	
	
	/**
	 * Run the Transformer associated with this handler.
	 * @param a_parameters parameters to the Transformer
	 * @return <code>true</code> if the run was successful, <code>false</code> otherwise
	 */
	public boolean run(Map a_parameters, boolean a_interactive) throws TransformerRunException {
		Transformer _transformer = null;
		try {
			_transformer = createTransformerObject(a_interactive);
		} catch (IllegalArgumentException e) {
			throw new TransformerRunException("Illegal argument", e);
		} catch (InstantiationException e) {
			throw new TransformerRunException("Instantiation problems", e);
		} catch (IllegalAccessException e) {
			throw new TransformerRunException("Illegal access", e);
		} catch (InvocationTargetException e) {
			throw new TransformerRunException("Invocation problem", e);
		} 
		
		// Turn the parameters to a simple key->value string map
		Map _params = new LinkedHashMap();
		for (Iterator _iter = a_parameters.keySet().iterator(); _iter.hasNext(); ) {
		    String _key = (String)_iter.next();
		    org.daisy.dmfc.core.script.Parameter _param = (org.daisy.dmfc.core.script.Parameter)a_parameters.get(_key);
		    _params.put(_key, _param.getValue());
		}
		
		return _transformer.execute(_params);
	}
	
	/**
	 * Checks if the parameters in a task script are valid for this Transformer.
	 * Also add any hard-coded parameters. 
	 * @param a_parameters a collection of parameters
	 */
	public void validateParameters(Map a_parameters) throws ValidationException {
		HashMap _map = new HashMap();

		// Add all hard-coded parameters in the TDF to the script parameters
		for (Iterator _iter = parameters.iterator(); _iter.hasNext(); ) {
		    Parameter _param = (Parameter)_iter.next();
			if (_param.getValue() != null) {
			    if (a_parameters.containsKey(_param.getName())) {			        
			        throw new ValidationException(i18n("PARAM_NOT_BY_USER", _param.getName()));
			    }
			    org.daisy.dmfc.core.script.Parameter _scriptParameter = new org.daisy.dmfc.core.script.Parameter(_param.getName(), _param.getValue()); 
			    a_parameters.put(_param.getName(), _scriptParameter);
			}
		}
		
		// Make sure there are no parameters in the script file that is not in the TDF.
		for (Iterator _iter = parameters.iterator(); _iter.hasNext(); ) {
			Parameter _transformerParam = (Parameter)_iter.next();			
			_map.put(_transformerParam.getName(), _transformerParam);
		}		
		for (Iterator _iter = a_parameters.values().iterator(); _iter.hasNext(); ) {
			org.daisy.dmfc.core.script.Parameter _scriptParameter = (org.daisy.dmfc.core.script.Parameter)_iter.next();
			Parameter _transformerParameter = (Parameter)_map.get(_scriptParameter.getName());
			if (_transformerParameter == null) {
				throw new ValidationException("Parameter " + _scriptParameter.getName() + " in script file is not recognized by Transformer " + getName());
			}
		}		
		
		// Make sure there are no required parameters in the TDF that are not present in the script file
		for (Iterator _iter = parameters.iterator(); _iter.hasNext(); ) {
			Parameter _transformerParam = (Parameter)_iter.next();
			if (_transformerParam.isRequired()) {
				org.daisy.dmfc.core.script.Parameter _scriptParam = (org.daisy.dmfc.core.script.Parameter)a_parameters.get(_transformerParam.getName());
				if (_scriptParam == null) {
					throw new ValidationException("Parameter " + _transformerParam.getName() + " is required by the Transformer " + getName());
				}
			} else {
			    org.daisy.dmfc.core.script.Parameter _scriptParam = (org.daisy.dmfc.core.script.Parameter)a_parameters.get(_transformerParam.getName());
				if (_scriptParam == null) {
					a_parameters.put(_transformerParam.getName(), new org.daisy.dmfc.core.script.Parameter(_transformerParam.getName(), _transformerParam.getDefaultValue()));
				}
			}
		}
	}
	
	/**
	 * Tries to find and load the Java class associated with this Transformer.
	 * @param a_transformerDescription
	 * @throws ClassNotFoundException
	 */
	private void createTransformerClass(File a_transformerDescription) throws ClassNotFoundException {
	    sendMessage(Level.FINE, i18n("LOADING_TRANSFORMER", name, classname));
		File _dir = a_transformerDescription.getAbsoluteFile();
		_dir = _dir.getParentFile();
		transformerClassLoader = new DirClassLoader(new File("transformers"), _dir);
		transformerClass = Class.forName(classname, true, transformerClassLoader);
	}
	
	/**
	 * Creates an instance object of the Transformer class.
	 * @param a_interactive
	 * @return a <code>Transformer</code> object
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private Transformer createTransformerObject(boolean a_interactive) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {		
		Object _params[] = {inputListener, this.getEventListeners(), new Boolean(a_interactive)};
		Transformer _trans = (Transformer)transformerConstructor.newInstance(_params);
		return _trans;
	}
	
	/**
	 * Reads the properties in the TDF.
	 * @param a_element
	 * @throws MIMEException
	 */
	private void readProperties(Element a_element) throws MIMEException {
		this.name = a_element.valueOf("name");
		this.description = a_element.valueOf("description");
		this.classname = a_element.valueOf("classname");
		this.version = a_element.valueOf("@version");
		
		XPath _xpathSelector = DocumentHelper.createXPath("parameters/parameter");
		List _parameters = _xpathSelector.selectNodes(a_element);
		for (Iterator _iter = _parameters.iterator(); _iter.hasNext(); ) {
			Element _parameter = (Element)_iter.next();
			Parameter _param = new Parameter(_parameter);
			parameters.add(_param);
		}
	}
	
	/**
	 * Makes sure the constructor we wish to use exists.
	 * @throws NoSuchMethodException
	 */
	private void checkForTransformerConstructor() throws NoSuchMethodException {
		Class[] _params = {InputListener.class, Set.class, Boolean.class};
		transformerConstructor = transformerClass.getConstructor(_params);
	}
	
	/**
	 * Calls the static method <code>isSupported</code> in the Transformer class
	 * and returns the result.
	 * @return
	 * @throws NoSuchMethodException
	 * @throws TransformerRunException
	 */
	private boolean transformerSupported() throws NoSuchMethodException, TransformerRunException {
		Method _isSupportedMethod = transformerClass.getMethod("isSupported", null);
		Boolean _result = new Boolean(true);
		try {
			_result = (Boolean)_isSupportedMethod.invoke(null, null);
		} catch (IllegalArgumentException e) {
			throw new TransformerRunException("Illegal argument", e);
		} catch (IllegalAccessException e) {
			throw new TransformerRunException("Illegal access", e);
		} catch (InvocationTargetException e) {
			throw new TransformerRunException("Invocation problem", e);
		}
		return _result.booleanValue();
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
	    Vector _params = (Vector)parameters.clone();	    
	    // Remove all hard coded parameters
	    for (int i = 0; i < parameters.size(); ++i) {
	        ParameterInfo _param = (ParameterInfo)parameters.elementAt(i);
	        if (_param.getValue() != null) {
	            //System.err.println("Removing hard coded param " + _param.getName());
	            _params.remove(_param);
	        }
	    }
		return _params;
	}	
	
	/**
	 * Checks if the current platform is supported by this Transformer
	 * @param a_element
	 * @return <code>true</code> if the platform is supported, <code>false</code> otherwise
	 */
	private boolean isPlatformOk(Element a_element) {
		boolean _ret = true;
		XPath _xpathSelector = DocumentHelper.createXPath("platforms/platform");
		List _platforms = _xpathSelector.selectNodes(a_element);
		if (_platforms.size() > 0) {
			_ret = false;
		}
		for (Iterator _iter = _platforms.iterator(); _iter.hasNext(); ) {			
			Element _platform = (Element)_iter.next();
			boolean _platformOk = true;
			for (Iterator _properties = _platform.elementIterator("property"); _properties.hasNext(); ) {
				Element _property = (Element)_properties.next();
				String _name = _property.valueOf("name");
				String _value = _property.valueOf("value");
				String _real = System.getProperty(_name);
				if (_real == null) {
					_platformOk = false;
					sendMessage(Level.WARNING, "Unknown property: '" + _name + "'");
				}
				else {
					if (!_real.matches(_value)) {
						_platformOk = false;
					}
					//System.err.println("Property: " + _name + ", value: " + _value + ", real: " + _real);
				}
			}
			if (_platformOk) {
				_ret = true;
			}
		}
		return _ret;
	}
	
	public String getParameterType(String a_name) {
		for (Iterator _iter = parameters.iterator(); _iter.hasNext(); ) {
			Parameter _param = (Parameter)_iter.next();
			if (a_name.equals(_param.getName())) {
				return _param.getType();
			}
		}
		return null;
	}
}
