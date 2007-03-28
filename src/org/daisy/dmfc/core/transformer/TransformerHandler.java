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
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.DirClassLoader;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.event.CoreMessageEvent;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.dmfc.exception.NotSupposedToHappenException;
import org.daisy.dmfc.exception.TransformerDisabledException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.i18n.I18n;
import org.daisy.util.mime.MIMEException;
import org.daisy.util.mime.MIMETypeRegistryException;
import org.daisy.util.xml.validation.ValidationException;
import org.daisy.util.xml.validation.Validator;

/**
 * Handles descriptions of and initiates the execution of Transformers.
 * The TransformerHandler class is responsible for parsing the Transformer description
 * file (TDF), finding the Java class associated with the Transformer and loading it.
 * It is also responsible for verifying that the parameters sent from the execution
 * script matches the parameters described in the TDF.
 *   
 * @author Linus Ericson
 */
public class TransformerHandler implements TransformerInfo {

	private String name;
	private String description;
	private String classname;
	private Set jars = new HashSet();
	private String version;
	private Vector parameters = new Vector();
	private boolean platformSupported = true;
	
	private I18n mInternationalization;
	
	private InputListener inputListener;
		
	private DirClassLoader transformerClassLoader;
	private Class transformerClass;
	private Constructor transformerConstructor;
	
	private File transformerDirectory;
	
	/**
	 * Creates a Transformer handler.
	 * @param transformerDescription a transformer description file
	 * @param inListener an input listener
	 * @throws TransformerDisabledException
	 */
	public TransformerHandler(File transformerDescription, InputListener inListener, Validator validator) throws TransformerDisabledException {
		
		mInternationalization = new I18n();
		inputListener = inListener;		
		transformerDirectory = transformerDescription.getParentFile();
		
		/*
		 * If any validation or dependency check fails, disable this Transformer 
		 */
		try {						
			// Validate the transformer description file
			if (!validator.isValid(transformerDescription)) {
			    throw new TransformerDisabledException(i18n("TDF_NOT_VALID"));
			}
						
			// Read properties using StAX
			XMLInputFactory factory = XMLInputFactory.newInstance();
	        factory.setProperty("javax.xml.stream.isCoalescing", Boolean.TRUE);
	        factory.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
	        XMLEventReader er = factory.createXMLEventReader(new FileInputStream(transformerDescription));
	        
	        readProperties(er);
	        
	        if (!platformSupported) {
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
        } catch (IOException e) {
            throw new TransformerDisabledException(i18n("TDF_IO_EXCEPTION"), e);
        } catch (XMLStreamException e) {
            throw new TransformerDisabledException(i18n("PROBLEMS_PARSING_TDF"), e);            
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
		/*Map params = new LinkedHashMap();
		for (Iterator it = runParameters.entrySet().iterator(); it.hasNext(); ) {
		    Map.Entry entry = (Map.Entry)it.next();
		    params.put(entry.getKey(), ((org.daisy.dmfc.core.script.Parameter)entry.getValue()).getValue());
		}
		*/		
		//return transformer.executeWrapper(params, transformerDirectory);
		return transformer.executeWrapper(runParameters, transformerDirectory);
	}
	
	/**
	 * Tries to find and load the Java class associated with this Transformer.
	 * @param transformerDescription
	 * @throws ClassNotFoundException
	 */
	private void createTransformerClass(File transformerDescription) throws ClassNotFoundException {	    
	    EventBus.getInstance().publish(new CoreMessageEvent(this,i18n("LOADING_TRANSFORMER", name, classname),MessageEvent.Type.INFO));
		File dir = transformerDescription.getAbsoluteFile();
		dir = dir.getParentFile();
		transformerClassLoader = new DirClassLoader(new File(DMFCCore.getHomeDirectory(), "transformers"), dir);
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
		 
		/*
		 * mg20070327; we check which constructor (old with listener set, or new without)
		 * this transformer supports. 
		 */
		
		List<Object> params = new LinkedList<Object>();
		if(transformerConstructor.getParameterTypes().length == 2) {
			params.add(inputListener);
			params.add(Boolean.valueOf(interactive));
		}else{
			params.add(inputListener);
			params.add(new HashSet());	//this is the dummy no longer used but kept for Transformer backwards compatibility
			params.add(Boolean.valueOf(interactive));
		}
		
		Transformer trans = (Transformer)transformerConstructor.newInstance(params.toArray());
		trans.setTransformerInfo(this);
		return trans;
	}	
	

	/**
	 * Loop over all &lt;parameter&gt; elements. This method assumes the
	 * &lt;parameters&gt; start tag has just been read from the XML event reader.
	 * @param er the XML event reader
	 * @throws MIMEException
	 * @throws XMLStreamException
	 * @throws MIMETypeRegistryException 
	 */
	private void loopParameters(XMLEventReader er) throws MIMEException, XMLStreamException, MIMETypeRegistryException {
	    while (er.hasNext()) {
	        XMLEvent event = er.nextEvent();
	        switch (event.getEventType()) {
	        case XMLStreamConstants.START_ELEMENT:
	            StartElement se = event.asStartElement();	
	        	if (se.getName().getLocalPart().equals("parameter")) {
	        	    Parameter param = new Parameter(se, er, transformerDirectory);
	        	    parameters.add(param);
	        	}
	            break;	        
	        case XMLStreamConstants.END_ELEMENT:
	            EndElement ee = event.asEndElement();
	        	if (ee.getName().getLocalPart().equals("parameters")) {
	        	    // Stop when the </parameters> end tag is found. 
	        	    return;
	        	}
	            break;
	        }
	    }
	    throw new NotSupposedToHappenException("Did not find </parameters> end tag in TDF");
	}
	
	/**
	 * Reads the properties in the TDF.
	 * @param er an <code>XMLEventReader</code>.
	 * @throws MIMEException
	 * @throws XMLStreamException
	 * @throws MIMETypeRegistryException 
	 */
	private void readProperties(XMLEventReader er) throws MIMEException, XMLStreamException, MIMETypeRegistryException {
	    String current = null;
	    while (er.hasNext()) {
	        XMLEvent event = er.nextEvent();
	        switch (event.getEventType()) {
	        case XMLStreamConstants.START_ELEMENT:
	            StartElement se = event.asStartElement();
	        	String seName = se.getName().getLocalPart();
		       	if (seName.equals("transformer")) {
		            current = "transformer";
		            Attribute att = se.getAttributeByName(new QName("version"));
		            version = att.getValue();
		        } else if (seName.equals("name")) {
		            current = "name";	                
		        } else if (seName.equals("description")) {
		            current = "description";
		        } else if (seName.equals("classname")) {
		            current = "classname";
		        } else if (seName.equals("jar")) {
		            current = "jar";
		        } else if (seName.equals("parameters")) {
		            loopParameters(er);
		        } else if (seName.equals("platforms")) {
		            platformSupported = isPlatformOk(er);
		        }
	            break;
	        case XMLStreamConstants.CHARACTERS:
	            String data = event.asCharacters().getData();
	        	if (current == null) {
	        	    break;
	        	}
	            if (current.equals("name")) {
	                name = data;
	            } else if (current.equals("description")) {
	                description = data;
	            } else if (current.equals("classname")) {
	                classname = data;
	            } else if (current.equals("jar")) {
	                jars.add(data);
	            }
	            break;
	        case XMLStreamConstants.END_ELEMENT:
	            current = null;
	            break;
	        }        
	    }
	}
	
	/**
	 * Makes sure the constructor we wish to use exists.
	 * @throws NoSuchMethodException
	 */
	private void checkForTransformerConstructor() throws NoSuchMethodException {
		/*
		 * mg20070327: first we check for the 'new' constructor that doesnt
		 * take the deprecated set of EventListener
		 */
		Class[] params = {InputListener.class, Boolean.class};
		try{
			transformerConstructor = transformerClass.getConstructor(params);
		}catch (NoSuchMethodException nsme) {
			Class[] params2 = {InputListener.class, Set.class, Boolean.class};
			transformerConstructor = transformerClass.getConstructor(params2);
		}	
		
		
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
	
	public File getTransformerDir() {
	    return transformerDirectory;
	}
	
	public Collection getDocumentation() {
	    Collection coll = new ArrayList();
	    File doc = new File(getTransformerDir(), "doc.html");
        if (doc.canRead()) {
            coll.add(new Documentation(doc.toURI(), "Documentation"));
        }	    
        return coll;
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
	 * Checks if the current platform is supported by this Transformer.
	 * This method assumes the &lt;platforms&gt; start tag has just been read
	 * from the XMLEventReader.
	 * @param er the XML event reader to read the platform specification from
	 * @return <code>true</code> if the platform is supported, <code>false</code> otherwise
	 * @throws XMLStreamException
	 */
	private boolean isPlatformOk(XMLEventReader er) throws XMLStreamException {
	    /*
	     * Only one of the <platform> sub elements needs to evaluate to true
	     * for the current platform to be considered as supported. Therefore,
	     * the result is initialized to false and set to true once a
	     * <platform> element that evaluates to true if found.
	     */
	    boolean result = false;
	    while (er.hasNext()) {
	        XMLEvent event = er.nextEvent();
	        switch (event.getEventType()) {
	        case XMLStreamConstants.START_ELEMENT:
	            StartElement se = event.asStartElement();	
	        	String seName = se.getName().getLocalPart();
	        	if (seName.equals("platform")) {
	        	    if (checkSinglePlatformElement(er)) {
	        	        result = true;
	        	        /*
	        	         * Continue reading an processing events until the
	        	         * <platforms> end tag is found. That way we leave
	        	         * the XMLEventReader in a nice state and we are able
	        	         * to validate the rest of the platform checks.
	        	         */	        	        
	        	    }
	        	}
	            break;	        
	        case XMLStreamConstants.END_ELEMENT:
	            EndElement ee = event.asEndElement();
	        	String eeName = ee.getName().getLocalPart();
	        	if (eeName.equals("platforms")) {
	        	    return result;
	        	}
	            break;
	        }
	    }
	    throw new NotSupposedToHappenException("Did not find </platforms> end tag in TDF");
	}
	
	/**
	 * Evaluates a single &lt;platform&gt; element.
	 * This method assumes a &lt;platform&gt; start tag has just been read
	 * from the XMLEventReader.
	 * @param er the XML event reader to read the platform information from.
	 * @return <code>true</code> if the platform is supported, <code>false</code> otherwise
	 * @throws XMLStreamException
	 */
	private boolean checkSinglePlatformElement(XMLEventReader er) throws XMLStreamException {
	    /*
	     * All <property> elements within a <platform> must evaluate to true
	     * for the platform element to evaluate to true. Therefore the result
	     * is initailized to true and set to false once a property that
	     * evaluates to false is found.
	     */
	    boolean result = true;
	    String propertyName = null;
	    String propertyValue = null;
	    String current = null;
	    while (er.hasNext()) {
	        XMLEvent event = er.nextEvent();
	        switch (event.getEventType()) {
	        case XMLStreamConstants.START_ELEMENT:
	            StartElement se = event.asStartElement();	
	        	String seName = se.getName().getLocalPart();
	        	if (seName.equals("property")) {
	        	    propertyName = null;
	        	    propertyValue = null;
	        	} else if (seName.equals("name")) {
	        	    current = "name";
	        	} else if (seName.equals("value")) {
	        	    current = "value";
	        	}
	            break;
	        case XMLStreamConstants.CHARACTERS:
	            String data = event.asCharacters().getData();
	        	if (current == null) {
	        	    break;
	        	}
	            if (current.equals("name")) {
	                propertyName = data;
	            } else if (current.equals("value")) {
	                propertyValue = data;
	            } 
	            break;
	        case XMLStreamConstants.END_ELEMENT:
	            EndElement ee = event.asEndElement();
	        	String eeName = ee.getName().getLocalPart();	        	
	        	if (eeName.equals("property")) {
	        	    /*
	        	     * Once a </property> end tag is found, a property can be
	        	     * evaluated. Continue processing events until the </platform>
	        	     * end tag is found even if the result is set to false so the
	        	     * XMLEventReader is left in a nice state and all properties
	        	     * can be validated.
	        	     */
	        	    String realValue = System.getProperty(propertyName);
	        	    try {
						if (realValue == null) {													
							EventBus.getInstance().publish(new CoreMessageEvent(this,i18n("UNKNOWN_PROPERTY", propertyName),MessageEvent.Type.WARNING));
							result = false;
						} else if (!realValue.matches(propertyValue)) {						    
						    result = false;
						}
	        	    } catch (PatternSyntaxException e) {
	        	    	EventBus.getInstance().publish(new CoreMessageEvent(this,i18n("INCORRECT_PROPERTY_VALUE", propertyName),MessageEvent.Type.WARNING));	        	        
	        	        result = false;
	        	    }
	        	} else if (eeName.equals("platform")) {
	        	    return result;
	        	}
	        	current = null;
	            break;
	        }
	    }
	    throw new NotSupposedToHappenException("Did not find </platform> end tag in TDF");
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
	
	/*
	 * i18n convenience methods
	 */
	private String i18n(String msgId) {
		return mInternationalization.format(msgId);
	}

	private String i18n(String msgId, Object[] params) {
		return mInternationalization.format(msgId, params);
	}

	private String i18n(String msgId, Object param) {
		return i18n(msgId, new Object[]{param});
	}

	private String i18n(String msgId, Object param1, Object param2) {
		return i18n(msgId, new Object[]{param1, param2});
	}
}
