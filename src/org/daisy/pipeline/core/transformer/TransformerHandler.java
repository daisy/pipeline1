/*
 * DMFC - The DAISY Multi Format Converter Copyright (C) 2005 Daisy Consortium
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.daisy.pipeline.core.transformer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
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
import javax.xml.transform.TransformerException;

import org.daisy.pipeline.core.DirClassLoader;
import org.daisy.pipeline.core.InputListener;
import org.daisy.pipeline.core.event.CoreMessageEvent;
import org.daisy.pipeline.core.event.EventBus;
import org.daisy.pipeline.core.event.MessageEvent;
import org.daisy.pipeline.core.event.UserEvent;
import org.daisy.pipeline.core.script.Task;
import org.daisy.pipeline.exception.NotSupposedToHappenException;
import org.daisy.pipeline.exception.TransformerDisabledException;
import org.daisy.pipeline.exception.TransformerRunException;
import org.daisy.util.i18n.I18n;
import org.daisy.util.mime.MIMEException;
import org.daisy.util.mime.MIMETypeRegistryException;
import org.daisy.util.xml.pool.PoolException;
import org.daisy.util.xml.pool.StAXInputFactoryPool;
import org.daisy.util.xml.validation.SimpleValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Handles descriptions of and initiates the execution of Transformers. The
 * TransformerHandler class is responsible for parsing the Transformer
 * description file (TDF), finding the Java class associated with the
 * Transformer and loading it. It is also responsible for verifying that the
 * parameters sent from the execution script matches the parameters described in
 * the TDF.
 * 
 * @author Linus Ericson
 */
public class TransformerHandler implements TransformerInfo, ErrorHandler {

    private String mNiceName;
    private String mDescription;
    private String mClassname;
    private Set mJars = new HashSet();
    private Vector mParameters = new Vector();
    private boolean mPlatformSupported = true;
    private File mHomeDir = null;
    private String mVersion = null;
    private I18n mInternationalization;
    private InputListener mInputListener;
    private DirClassLoader mTransformerClassLoader;
    private Class mTransformerClass;
    private Constructor mTransformerConstructor;
    private File mTransformerDirectory;
    private boolean mLoadedFromJar = false;
    private static Map<String,Object> xifProperties = null;
    private URL mTdfUrl = null;
    private URI mDocumentationURI = null;
    
    private static SimpleValidator mTdfValidator = null;        
	private boolean mValidationError = false;

    /**
     * Creates a Transformer handler. This is the directory version.
     * 
     * @param tdfFile a transformer description file
     * @param inListener an input listener
     * @throws TransformerDisabledException
     */
    public TransformerHandler(File tdfFile, File transformersDir,
            InputListener inListener) throws TransformerDisabledException {

        mInternationalization = new I18n();
        mInputListener = inListener;
        mTransformerDirectory = tdfFile.getParentFile();
        mHomeDir = transformersDir.getParentFile();

        /*
         * If any validation or dependency check fails, disable this Transformer
         */
        try {
            initialize(tdfFile.toURI().toURL());

            // Create the class loader and Transformer class (not object)
            createTransformerClass(tdfFile, transformersDir);

            // Make sure the right constructor is present
            checkForTransformerConstructor();

            // Do dependency checks in the class associated with the Transformer
            if (!transformerSupported()) {
                throw new TransformerDisabledException(
                        i18n("TRANSFORMER_NOT_SUPPORTED"));
            }
        } catch (ClassNotFoundException e) {
            throw new TransformerDisabledException(i18n("CANNOT_CREATE_TRANSFORMER_CLASS"), e);
        } catch (NoSuchMethodException e) {
            throw new TransformerDisabledException(i18n("NOSUCHMETHOD_IN_TRANSFORMER"), e);
        } catch (TransformerRunException e) {
            throw new TransformerDisabledException("Cannot run static isSupported method of Transformer", e);
        } catch (MIMEException e) {
            throw new TransformerDisabledException("MIME exception", e);
        } catch (IOException e) {
            throw new TransformerDisabledException(i18n("TDF_IO_EXCEPTION"), e);
        } catch (XMLStreamException e) {
            throw new TransformerDisabledException(i18n("PROBLEMS_PARSING_TDF"), e);
        } catch (SAXException e) {
            throw new TransformerDisabledException(e.getMessage(), e);
        } catch (ValidationException e) {
			//tdf validation
        	throw new TransformerDisabledException(i18n("TDF_IO_EXCEPTION"), e);
		} catch (TransformerException e) {
			//tdf validation
			throw new TransformerDisabledException(i18n("TDF_IO_EXCEPTION"), e);
		}catch (PoolException e) {
			throw new TransformerDisabledException(i18n("TDF_IO_EXCEPTION"), e);
		}
    }

    /**
     * Creates a Transformer handler. This is the JAR version.
     * 
     * @param jarFile
     * @param transformerName
     * @param inListener
     * @param isJar
     * @throws TransformerDisabledException
     */
    public TransformerHandler(File jarFile, String transformerName,
            InputListener inListener, boolean isJar)
            throws TransformerDisabledException {
        mInternationalization = new I18n();
        mInputListener = inListener;
        mTransformerDirectory = null;
        mLoadedFromJar = true;
        /*
         * If any validation or dependency check fails, disable this Transformer
         */
        try {
            URL tdfUrl = new URL("jar:" + jarFile.toURI().toURL() + "!/" + transformerName + "/transformer.tdf");
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, this.getClass().getClassLoader());
            initialize(tdfUrl);
            // Create the class loader and Transformer class (not object)
            createTransformerClass(urlClassLoader);
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
        } catch (MIMEException e) {
            throw new TransformerDisabledException("MIME exception", e);
        } catch (IOException e) {
            throw new TransformerDisabledException(i18n("TDF_IO_EXCEPTION"), e);
        } catch (XMLStreamException e) {
            throw new TransformerDisabledException(i18n("PROBLEMS_PARSING_TDF"), e);
        } catch (SAXException e) {
            throw new TransformerDisabledException(e.getMessage(), e);
        } catch (ValidationException e) {
        	throw new TransformerDisabledException(i18n("TDF_IO_EXCEPTION"), e);
		} catch (TransformerException e) {
			throw new TransformerDisabledException(i18n("TDF_IO_EXCEPTION"), e);
		}catch (PoolException e) {
			throw new TransformerDisabledException(i18n("TDF_IO_EXCEPTION"), e);
		}
    }

    
    
    private void initialize(URL url) throws IOException, SAXException,
            TransformerDisabledException, XMLStreamException,
            MIMETypeRegistryException, MIMEException, TransformerException, ValidationException, PoolException {
    	
    	mTdfUrl = url;
    	
        //initialize the TDF validator    	
    	if(mTdfValidator==null){
    		mTdfValidator = new SimpleValidator(getClass().getResource("transformer-1.1.rng"), this);
    	}
    	//Validate the transformer description file
    	if (!mTdfValidator.validate(mTdfUrl) || mValidationError) {
            throw new TransformerDisabledException(i18n("TDF_NOT_VALID"));
        }

        // Read properties using StAX
    	if(xifProperties==null) {
    		xifProperties = new HashMap<String,Object>();
    		xifProperties.put(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    		xifProperties.put(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
    		xifProperties.put(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
    	}
    	
    	XMLInputFactory factory = null;
    	
    	try{
	    	factory = StAXInputFactoryPool.getInstance().acquire(xifProperties);         
	        XMLEventReader er = factory.createXMLEventReader(mTdfUrl.openStream());
	        readProperties(er);
		}finally{
			StAXInputFactoryPool.getInstance().release(factory, xifProperties);
		}
		
        if (!mPlatformSupported) {
            throw new TransformerDisabledException(i18n("PLATFORM_CHECK_FAILED"));
        }
    }

    /**
     * Run the Transformer associated with this handler.
     * 
     * @param runParameters parameters to the Transformer
     * @return <code>true</code> if the run was successful, <code>false</code>
     *         otherwise
     */
    public boolean run(Map runParameters, boolean interactive, Task task) throws TransformerRunException {
        Transformer transformer = null;
        try {
            transformer = createTransformerObject(interactive, task);
        } catch (IllegalArgumentException e) {
            throw new TransformerRunException(i18n("TRANSFORMER_ILLEGAL_ARGUMENT"), e);
        } catch (InstantiationException e) {
            throw new TransformerRunException("Instantiation problems", e);
        } catch (IllegalAccessException e) {
            throw new TransformerRunException(i18n("TRANSFORMER_ILLEGAL_ACCESS"), e);
        } catch (InvocationTargetException e) {
            throw new TransformerRunException(i18n("TRANSFORMER_INVOCATION_PROBLEM"), e);
        }

		EventBus.getInstance().subscribe(transformer, UserEvent.class);
		boolean res=false;
        try {
			res=transformer.executeWrapper(runParameters, mTransformerDirectory);
		} finally {
			EventBus.getInstance().unsubscribe(transformer, UserEvent.class);
		}
        return res;
    }

    /**
     * Tries to find and load the Java class associated with this Transformer.
     * 
     * @param tdfFile
     * @throws ClassNotFoundException
     */
    private void createTransformerClass(File tdfFile, File transformersDir) throws ClassNotFoundException {
    	
        EventBus.getInstance().publish(
                new CoreMessageEvent(this, i18n("LOADING_TRANSFORMER", mNiceName,
                        mClassname), MessageEvent.Type.DEBUG));
        
        File dir = tdfFile.getAbsoluteFile();
        dir = dir.getParentFile();
        mTransformerClassLoader = new DirClassLoader(transformersDir, transformersDir);
        
        for (Iterator it = mJars.iterator(); it.hasNext();) {
            String jar = (String) it.next();
            mTransformerClassLoader.addJar(new File(dir, jar));
        }
        
        mTransformerClass = Class.forName(mClassname, true,
                mTransformerClassLoader);
        
        URL codeSourceLocation = mTransformerClass.getProtectionDomain()
                .getCodeSource().getLocation();
        
        EventBus.getInstance().publish(
                new CoreMessageEvent(this, "Transformer loaded from "
                        + codeSourceLocation, MessageEvent.Type.DEBUG));
    }

    /**
     * Tries to find and load the Java class associated with this Transformer.
     * 
     * @throws ClassNotFoundException
     */
    private void createTransformerClass(ClassLoader cl) throws ClassNotFoundException {
    	
        EventBus.getInstance().publish(
                new CoreMessageEvent(this, i18n("LOADING_TRANSFORMER", mNiceName,
                        mClassname), MessageEvent.Type.INFO));
        
        mTransformerClass = Class.forName(mClassname, true, cl);
        
        URL codeSourceLocation = mTransformerClass.getProtectionDomain().getCodeSource().getLocation();
        
        EventBus.getInstance().publish(
                new CoreMessageEvent(this, "Transformer loaded from "
                        + codeSourceLocation, MessageEvent.Type.DEBUG));
        
        if (!codeSourceLocation.toExternalForm().endsWith(".jar") && mLoadedFromJar) {
            System.err.println("System error: JAR and directory mixup!");
        }
    }

    /**
     * Creates an instance object of the Transformer class.
     * 
     * @param interactive
     * @return a <code>Transformer</code> object
     * @throws IllegalArgumentException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Transformer createTransformerObject(boolean interactive, Task task)
            throws IllegalArgumentException, InstantiationException,
            IllegalAccessException, InvocationTargetException {

        /*
         * mg20070327; we check which constructor (old with listener set, or new
         * without) this transformer supports.
         */

        List<Object> params = new LinkedList<Object>();
        if (mTransformerConstructor.getParameterTypes().length == 2) {
            params.add(mInputListener);
            params.add(Boolean.valueOf(interactive));
        } else {
            params.add(mInputListener);
            params.add(new HashSet()); // this is the dummy no longer used but
                                        // kept for Transformer backwards
                                        // compatibility
            params.add(Boolean.valueOf(interactive));
        }

        Transformer trans = (Transformer) mTransformerConstructor
                .newInstance(params.toArray());
        trans.setTransformerInfo(this);
        trans.setLoadedFromJar(mLoadedFromJar);
        trans.setTask(task);
        return trans;
    }

    /**
     * Loop over all &lt;parameter&gt; elements. This method assumes the
     * &lt;parameters&gt; start tag has just been read from the XML event
     * reader.
     * 
     * @param er the XML event reader
     * @throws MIMEException
     * @throws XMLStreamException
     * @throws MIMETypeRegistryException
     */
    private void loopParameters(XMLEventReader er) throws MIMEException,
            XMLStreamException, MIMETypeRegistryException {
        while (er.hasNext()) {
            XMLEvent event = er.nextEvent();
            switch (event.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                StartElement se = event.asStartElement();
                if (se.getName().getLocalPart().equals("parameter")) {
                    Parameter param = new Parameter(se, er, mTransformerDirectory);
                    mParameters.add(param);
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
        throw new NotSupposedToHappenException(
                "Did not find </parameters> end tag in TDF");
    }

    /**
     * Reads the properties in the TDF.
     * 
     * @param er an <code>XMLEventReader</code>.
     * @throws MIMEException
     * @throws XMLStreamException
     * @throws MIMETypeRegistryException
     */
    private void readProperties(XMLEventReader er) throws MIMEException,
            XMLStreamException, MIMETypeRegistryException {
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
                    mVersion = att.getValue();
                } else if (seName.equals("name")) {
                    current = "name";
                } else if (seName.equals("description")) {
                    current = "description";
                } else if (seName.equals("classname")) {
                    current = "classname";
                } else if (seName.equals("documentation")) {
                	Attribute att = se.getAttributeByName(new QName("uri"));
                    try {
						mDocumentationURI = mTdfUrl.toURI().resolve(att.getValue());						
					} catch (Exception e) {
					      EventBus.getInstance().publish(
					                new CoreMessageEvent(this, i18n("DOCUMENTATION_URI_FAILURE", mNiceName,
					                        att.getValue()), MessageEvent.Type.ERROR,MessageEvent.Cause.SYSTEM));
					}
                } else if (seName.equals("jar")) {
                    current = "jar";
                } else if (seName.equals("parameters")) {
                    loopParameters(er);
                } else if (seName.equals("platforms")) {
                    mPlatformSupported = isPlatformOk(er);
                }
                break;
            case XMLStreamConstants.CHARACTERS:
                String data = event.asCharacters().getData();
                if (current == null) {
                    break;
                }
                if (current.equals("name")) {
                    mNiceName = data;
                } else if (current.equals("description")) {
                    mDescription = data;
                } else if (current.equals("classname")) {
                    mClassname = data;
                } else if (current.equals("jar")) {
                    mJars.add(data);
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
     * 
     * @throws NoSuchMethodException
     */
    private void checkForTransformerConstructor() throws NoSuchMethodException {
        /*
         * mg20070327: first we check for the 'new' constructor that doesnt take
         * the deprecated set of EventListener
         */
        Class[] params = { InputListener.class, Boolean.class };
        try {
            mTransformerConstructor = mTransformerClass.getConstructor(params);
        } catch (NoSuchMethodException nsme) {
            Class[] params2 = { InputListener.class, Set.class, Boolean.class };
            mTransformerConstructor = mTransformerClass.getConstructor(params2);
        }

    }

    /**
     * Calls the static method <code>isSupported</code> in the Transformer
     * class and returns the result.
     * 
     * @return
     * @throws NoSuchMethodException
     * @throws TransformerRunException
     */
    private boolean transformerSupported() throws NoSuchMethodException,
            TransformerRunException {
        Method isSupportedMethod = mTransformerClass.getMethod("isSupported",
                (Class[]) null);
        Boolean result;
        try {
            result = (Boolean) isSupportedMethod.invoke(null, (Object[]) null);
        } catch (IllegalArgumentException e) {
            throw new TransformerRunException(
                    i18n("TRANSFORMER_ILLEGAL_ARGUMENT"), e);
        } catch (IllegalAccessException e) {
            throw new TransformerRunException(
                    i18n("TRANSFORMER_ILLEGAL_ACCESS"), e);
        } catch (InvocationTargetException e) {
            throw new TransformerRunException(
                    i18n("TRANSFORMER_INVOCATION_PROBLEM"), e);
        }
        return result.booleanValue();
    }

    public String getName() {
        return getNiceName();
    }

    public String getNiceName() {
        return mNiceName;
    }

    public String getPackageName() {
        return mClassname.substring(0, mClassname.lastIndexOf('.'));
    }

    public String getDescription() {
        return mDescription;
    }

    public File getTransformerDir() {
        return mTransformerDirectory;
    }

    public URI getDocumentation() {
    	return mDocumentationURI;
    }

    /**
     * Gets a collection of the parameters of the Transformer. 
     */
    public Collection getParameters() {
        return mParameters;
    }

    /**
     * Checks if the current platform is supported by this Transformer. This
     * method assumes the &lt;platforms&gt; start tag has just been read from
     * the XMLEventReader.
     * 
     * @param er the XML event reader to read the platform specification from
     * @return <code>true</code> if the platform is supported,
     *         <code>false</code> otherwise
     * @throws XMLStreamException
     */
    private boolean isPlatformOk(XMLEventReader er) throws XMLStreamException {
        /*
         * Only one of the <platform> sub elements needs to evaluate to true for
         * the current platform to be considered as supported. Therefore, the
         * result is initialized to false and set to true once a <platform>
         * element that evaluates to true if found.
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
                         * <platforms> end tag is found. That way we leave the
                         * XMLEventReader in a nice state and we are able to
                         * validate the rest of the platform checks.
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
        throw new NotSupposedToHappenException(
                "Did not find </platforms> end tag in TDF");
    }

    /**
     * Evaluates a single &lt;platform&gt; element. This method assumes a
     * &lt;platform&gt; start tag has just been read from the XMLEventReader.
     * 
     * @param er the XML event reader to read the platform information from.
     * @return <code>true</code> if the platform is supported,
     *         <code>false</code> otherwise
     * @throws XMLStreamException
     */
    private boolean checkSinglePlatformElement(XMLEventReader er)
            throws XMLStreamException {
        /*
         * All <property> elements within a <platform> must evaluate to true for
         * the platform element to evaluate to true. Therefore the result is
         * initailized to true and set to false once a property that evaluates
         * to false is found.
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
                     * evaluated. Continue processing events until the
                     * </platform> end tag is found even if the result is set to
                     * false so the XMLEventReader is left in a nice state and
                     * all properties can be validated.
                     */
                    String realValue = System.getProperty(propertyName);
                    try {
                        if (realValue == null) {
                            EventBus.getInstance().publish(
                                    new CoreMessageEvent(this, i18n(
                                            "UNKNOWN_PROPERTY", propertyName),
                                            MessageEvent.Type.WARNING));
                            result = false;
                        } else if (!realValue.matches(propertyValue)) {
                            result = false;
                        }
                    } catch (PatternSyntaxException e) {
                        EventBus.getInstance().publish(
                                new CoreMessageEvent(this, i18n(
                                        "INCORRECT_PROPERTY_VALUE",
                                        propertyName),
                                        MessageEvent.Type.WARNING));
                        result = false;
                    }
                } else if (eeName.equals("platform")) {
                    return result;
                }
                current = null;
                break;
            }
        }
        throw new NotSupposedToHappenException(
                "Did not find </platform> end tag in TDF");
    }

    public String getParameterType(String parameterName) {
        for (Iterator it = mParameters.iterator(); it.hasNext();) {
            Parameter param = (Parameter) it.next();
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
        return i18n(msgId, new Object[] { param });
    }

    private String i18n(String msgId, Object param1, Object param2) {
        return i18n(msgId, new Object[] { param1, param2 });
    }

    private String i18n(String msgId, Object param1, Object param2, Object param3) {
        return i18n(msgId, new Object[] { param1, param2, param3 });
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException e) throws SAXException {
        saxWarn(e);
        mValidationError = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException e) throws SAXException {
        saxWarn(e);
        mValidationError  = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException e) throws SAXException {
    	if(!e.getMessage().contains("XSLT 1.0")) {
    		//hack, avoid saxon 8 version warning messages
    		saxWarn(e);
    	}	        
    }

    private void saxWarn(SAXParseException e) {
        EventBus.getInstance().publish(
        		new CoreMessageEvent(this, i18n("SAX_VALIDATION_EXCEPTION",e.getSystemId(),e.getLineNumber(),e.getMessage()),
                MessageEvent.Type.WARNING, MessageEvent.Cause.INPUT));
    }
}
