/*
 * Created on 2005-mar-07
 */
package org.daisy.dmfc.core.transformer;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.daisy.dmfc.core.DirClassLoader;
import org.daisy.dmfc.core.EventSender;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.exception.TransformerDisabledException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.exception.ValidationException;
import org.daisy.util.i18n.I18n;
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
 * @author LINUSE
 */
public class TransformerHandler extends EventSender implements TransformerInfo {

	private String name;
	private String description;
	private String classname;
	private String version;
	private Collection parameters = new Vector();
	private boolean supported;
	
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
	public TransformerHandler(File a_transformerDescription, I18n a_i18n, InputListener a_inputListener, Set a_eventListeners, Validator a_validator) throws TransformerDisabledException {
		super(a_eventListeners);
		inputListener = a_inputListener;
		setI18n(a_i18n);
		
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
			throw new TransformerDisabledException(i18n("TDF_VALIDATION_EXECPTION"), e);
		}	
	}
	
	
	/**
	 * Run the Transformer associated with this handler.
	 * @param a_parameters parameters to the Transformer
	 * @return <code>true</code> if the run was successful, <code>false</code> otherwise
	 */
	public boolean run(Collection a_parameters, boolean a_interactive) throws TransformerRunException {		
		Transformer _transformer = null;
		try {
			validateParameters(a_parameters);
			_transformer = createTransformerObject(a_interactive);
		} catch (IllegalArgumentException e) {
			throw new TransformerRunException("Illegal argument", e);
		} catch (InstantiationException e) {
			throw new TransformerRunException("Instantiation problems", e);
		} catch (IllegalAccessException e) {
			throw new TransformerRunException("Illegal access", e);
		} catch (InvocationTargetException e) {
			throw new TransformerRunException("Invocation problem", e);
		} catch (ValidationException e) {
			throw new TransformerRunException("Parameters are invalid", e);
		}
		return _transformer.execute(a_parameters);
	}
	
	/**
	 * Checks if the parameters in a task script are valid for this Transformer
	 * @param a_parameters a collection of parameters
	 */
	public void validateParameters(Collection a_parameters) throws ValidationException {
		HashMap _map = new HashMap();
		
		// Make sure there are no parameters in the script file that is not in the TDF.
		for (Iterator _iter = parameters.iterator(); _iter.hasNext(); ) {
			Parameter _transformerParam = (Parameter)_iter.next();
			_map.put(_transformerParam.getName(), _transformerParam);
		}		
		for (Iterator _iter = a_parameters.iterator(); _iter.hasNext(); ) {
			org.daisy.dmfc.core.script.Parameter _scriptParameter = (org.daisy.dmfc.core.script.Parameter)_iter.next();
			Parameter _transformerParameter = (Parameter)_map.get(_scriptParameter.getName());
			if (_transformerParameter == null) {
				throw new ValidationException("Parameter " + _scriptParameter.getName() + " in script file is not recognized by Transformer " + getName());
			}
		}
		_map.clear();
		
		// Make sure there are no required parameters in the TDF that are not present in the script file
		for (Iterator _iter = a_parameters.iterator(); _iter.hasNext(); ) {
			org.daisy.dmfc.core.script.Parameter _scriptParam = (org.daisy.dmfc.core.script.Parameter)_iter.next();
			_map.put(_scriptParam.getName(), _scriptParam);
		}
		for (Iterator _iter = parameters.iterator(); _iter.hasNext(); ) {
			Parameter _transformerParam = (Parameter)_iter.next();
			if (_transformerParam.isRequired()) {
				org.daisy.dmfc.core.script.Parameter _scriptParam = (org.daisy.dmfc.core.script.Parameter)_map.get(_transformerParam.getName());
				if (_scriptParam == null) {
					throw new ValidationException("Parameter " + _transformerParam.getName() + " is required by the Transformer " + getName());
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
		sendMessage("About to create the Transformer class '" + classname + "'");
		File _dir = a_transformerDescription.getAbsoluteFile();
		/*
		boolean found = false;
		// FIXME magic number
		for (int i = 0; i < 3 && !found; ++i) {
			_dir = _dir.getParentFile();
			found = true;
			try {
				//transformerClassLoader = new DirClassLoader(_dir);
				transformerClassLoader = new DirClassLoader(new File("plugin"), _dir);
				transformerClass = Class.forName(classname, true, transformerClassLoader);
				sendMessage("Class " + classname + " found in " + _dir.getAbsolutePath());
			} catch (ClassNotFoundException e) {
				sendMessage("Class not found in " + _dir.getAbsolutePath());
				found = false;
			}
		}
		if (!found) {
			throw new ClassNotFoundException("Could not find class file " + classname + " for Transformer " + name);
		}
		*/
		_dir = _dir.getParentFile();
		transformerClassLoader = new DirClassLoader(new File("plugin"), _dir);
		transformerClass = Class.forName(classname, true, transformerClassLoader);
	}
	
	/**
	 * Creates an instance object of the Transformer class.
	 * @param a_interactive
	 * @return
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
	 */
	private void readProperties(Element a_element) {
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

	public Collection getParameters() {
		return parameters;
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
					sendMessage("Unknown property: '" + _name + "'");
				}
				else {
					if (!_real.matches(_value)) {
						_platformOk = false;
						System.err.print("-");
					}
					System.err.println("Property: " + _name + ", value: " + _value + ", real: " + _real);
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
