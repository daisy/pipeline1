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
package org.daisy.dmfc.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.exception.TransformerDisabledException;
import org.daisy.dmfc.logging.LoggingPropertiesReader;
import org.daisy.dmfc.logging.MessageLogger;
import org.daisy.util.file.TempFile;
import org.daisy.util.i18n.I18n;
import org.daisy.util.xml.validation.RelaxngSchematronValidator;
import org.daisy.util.xml.validation.ValidationException;
import org.daisy.util.xml.validation.Validator;


/**
 * This is the class users of DMFC should instantiate.
 * A common usage of DMFC would include the following:
 * <pre>
 * DMFCCore dmfc = new DMFCCore(inputListener, eventListener);
 * dmfc.executeScript(scriptFile);
 * </pre>
 * @author Linus Ericson
 */
public class DMFCCore extends EventSender {

    private static Pattern tdfFolderPattern = Pattern.compile(".*[^a-z]([a-z]+)_([a-z]+)_(.*)");
    private static File homeDirectory = null;
    
	private InputListener inputListener;
	private Map transformerHandlers = new HashMap();	
	
	/**
	 * Create an instance of DMFC using the default locale.
	 * This is the same as <code>new DMFCCore(a_inputListener, a_eventListener, new Locale("en"))</code>. 
	 * @param inListener
	 * @param evListener
	 * @throws DMFCConfigurationException
	 */
	public DMFCCore(InputListener inListener, EventListener evListener) throws DMFCConfigurationException {
	    this(inListener, evListener, new Locale("en"));
	}
	
	/**
	 * Create an instance of DMFC.
	 * @param inListener a listener of (user) input events
	 * @param evListener a listener of events
	 * @param locale the locale to use
	 * @throws DMFCConfigurationException
	 */
	public DMFCCore(InputListener inListener, EventListener evListener, Locale locale) throws DMFCConfigurationException {
		super(evListener);
		inputListener = inListener;
		Locale.setDefault(locale);
		
		/*System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
		System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
		System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration","com.sun.org.apache.xerces.internal.parsers.XML11Configuration");
			*/	
		// Set DMFC home dir
		homeDirectory = getHomeDir();
		System.setProperty("dmfc.home", getHomeDirectory().getAbsolutePath());
		
		// Load properties from file
		if (!loadProperties(ClassLoader.getSystemResourceAsStream("dmfc.properties"))) {
		    throw new DMFCConfigurationException("Can't read dmfc.properties!");
		}

		// Load messages
		DirClassLoader resourceLoader = new DirClassLoader(new File(getHomeDirectory(), "resources"), new File(getHomeDirectory(), "resources"));
		ResourceBundle bundle = ResourceBundle.getBundle("dmfc_messages", Locale.ENGLISH, resourceLoader);
		I18n.setDefaultBundle(bundle);				

		TempFile.setTempDir(new File(System.getProperty("dmfc.tempDir")));
			
		// Setup logging
		Logger lg = Logger.getLogger("");
        Handler[] handlers = lg.getHandlers();
        for (int i = 0; i < handlers.length; ++i) {
            lg.removeHandler(handlers[i]);
        }		
		MessageLogger logger = new MessageLogger();
		addEventListener(logger);
		LoggingPropertiesReader.addHandlers(logger, System.getProperty("dmfc.logging"));

		// Load the transformers
		if (!reloadTransformers()) {
		    throw new DMFCConfigurationException("Cannot load the transformers");
		}
	}	

	/**
	 * Find the home directory of DMFC.
	 * @return the home directory of DMFC.
	 * @throws DMFCConfigurationException
	 */
	private static File getHomeDir() throws DMFCConfigurationException {
	    URL url = ClassLoader.getSystemResource("dmfc.properties");
	    if (url == null) {
	        System.err.println("Can't find dmfc.properties");
	        throw new DMFCConfigurationException("Can't find dmfc.properties");
	    }
	    File dir;
        try {
            dir = new File(new URI(url.toExternalForm())).getParentFile().getParentFile();
        } catch (URISyntaxException e) {
            throw new DMFCConfigurationException("Can't create file object");
        }        
	    return dir;
	}
	
	/**
	 * Gets the home directory of DMFC.
	 * @return the home directory of DMFC or <code>null</code> if it has not yet been set.
	 */
	public static File getHomeDirectory() {
	    return homeDirectory;
	}
	
	/**
	 * Adds a set properties to the system properties.
	 * @param propertiesStream an InputStream
	 * @return <code>true</code> if the loading was successful, <code>false</code> otherwise
	 */
	public boolean loadProperties(InputStream propertiesStream) {
	    try {
	        Properties properties = new Properties(System.getProperties());
            properties.load(propertiesStream);         
            System.setProperties(properties);
        } catch (IOException e) {            
            e.printStackTrace();
            return false;
        }
	    return true;
	}
	
	/**
	 * Iterate over all Transformer Description Files (TDF) and
	 * load each Transformer.
	 * @return <code>true</code> if the reloading was successful, <code>false</code> otherwise.
	 */
	public boolean reloadTransformers() {
		try {
			Validator validator = new RelaxngSchematronValidator(new File(getHomeDirectory().getPath() + File.separator + "resources", "transformer.rng"), null,true,true);
			sendMessage(Level.CONFIG, i18n("RELOADING_TRANSFORMERS"));
			transformerHandlers.clear();		
			addTransformers(new File(getHomeDirectory(), "transformers"), validator);			
			sendMessage(Level.CONFIG, i18n("RELOADING_TRANSFORMERS_DONE"));
		} catch (ValidationException e) {
			sendMessage(Level.SEVERE, i18n("RELOADING_TRANSFORMERS_FAILED", e.getMessage()));
			e.printStackTrace();
			return false;
		}		
		return true;
	}
		
	/**
	 * Recursively add transformers as the transformer description files (TDFs) are found
	 * @param dir the directory to start searching in
	 * @param validator a Validator of TDFs
	 */
	private void addTransformers(File dir, Validator validator) {
		if (!dir.isDirectory()) {
		    sendMessage(Level.SEVERE, i18n("ADD_TRANSFORMER_NOT_DIRECTORY", dir.getAbsolutePath()));
			return;
		}
		File[] children = dir.listFiles();
		for (int i = 0; i < children.length; ++i) {
			File current = children[i];
			
			// Process subdirectories
			if (current.isDirectory()) {
				addTransformers(current, validator);
			}
			else if (current.getName().matches(".*\\.tdf")) {
			    String transformerName = getTransformerNameFromPath(current.getAbsolutePath());			    
			    if (transformerName != null) {
			        try {
						TransformerHandler th = new TransformerHandler(current, inputListener, getEventListeners(), validator);					
						if (transformerHandlers.containsKey(transformerName)) {
						    throw new TransformerDisabledException(i18n("TRANSFORMER_ALREADY_EXISTS", transformerName));						
						}
						transformerHandlers.put(transformerName, th);
					} catch (TransformerDisabledException e) {
					    sendMessage(Level.WARNING, i18n("TRANSFORMER_DISABLED", current.getAbsolutePath(), e.getMessage()));					
						if (e.getRootCause() != null) {
							sendMessage(Level.WARNING, i18n("ROOT_CAUSE", e.getRootCauseMessagesAsString()));
						}
					}
			    } else {
			        sendMessage(Level.WARNING, "TDF has incorrect folder pattern! " + current.getAbsolutePath());
			    }
			}
		}
	}
	
	/**
	 * Convert a TDF path to a transformer name. The nam created is the name
	 * scripts use to refer to the transformer.
	 * @param path path to a TDF.
	 * @return a transformer name.
	 */
	private String getTransformerNameFromPath(String path) {
	    Matcher tdfFolderMatcher = tdfFolderPattern.matcher(path);
	    if (tdfFolderMatcher.matches()) {	        
	        String countryCode = tdfFolderMatcher.group(1);
	        String organization = tdfFolderMatcher.group(2);
	        String localname = tdfFolderMatcher.group(3);
	        localname = localname.substring(0, localname.lastIndexOf(File.separator));
	        localname = localname.replace(File.separatorChar, '.');
	        return countryCode + "_" + organization + "_" + localname;
	    }
	    return null;
	}
	
	/**
	 * Gets a collection of the current <code>TransformerInfo</code> objects.
	 * @return a collection of <code>TransformerInfo</code> objects.
	 */
	public Collection getTransformerInfoCollection() {
	    return transformerHandlers.values();
	}
	
	/**
	 * Executes a task script.
	 * @param script the script to execute
	 * @return true if the exeution was successful, false otherwise.
	 */
	public boolean executeScript(File script) {		
		boolean ret = false;
		try {
			Validator validator = new RelaxngSchematronValidator(new File(getHomeDirectory().getPath() + File.separator + "resources", "script.rng"), null,true,false);
			ScriptHandler handler = new ScriptHandler(script, transformerHandlers, getEventListeners(), validator);
			handler.execute();
			ret = true;
		} catch (ScriptException e) {
		    sendMessage(Level.SEVERE, i18n("SCRIPT_EXCEPTION", e.getMessage()));
			if (e.getRootCause() != null) {
			    String msg = "";
			    String[] msgs = e.getRootCauseMessages();			    
			    for (int i = 0; i < msgs.length; ++i) {
			        msg = msgs[i] + "\n";
			    }
				sendMessage(Level.SEVERE, i18n("ROOT_CAUSE", msg));				
			}
		} catch (ValidationException e) {
		    sendMessage(Level.SEVERE, i18n("SCRIPT_VALIDATION_PROBLEM", e.getMessage()));
			if (e.getRootCause() != null) {
				sendMessage(Level.SEVERE, i18n("ROOT_CAUSE", e.getRootCause().getMessage()));
			}
		} catch (MIMEException e) {
			if (e.getRootCause() != null) {
			    String msg = "";
			    String[] msgs = e.getRootCauseMessages();			    
			    for (int i = 0; i < msgs.length; ++i) {
			        if (msgs[i] != null) { 
			            msg = msgs[i] + "\n";
			        }
			    }
				sendMessage(Level.SEVERE, i18n("ROOT_CAUSE", msg));
			}
        }
		return ret;
	}
	
	/**
	 * Validates a task script
	 * @param script the script to validate
	 * @throws ValidationException
	 * @throws ScriptException
	 * @throws MIMEException
	 */
	public void validateScript(File script) throws ValidationException, ScriptException, MIMEException {
		Validator validator = new RelaxngSchematronValidator(new File(getHomeDirectory().getPath() + File.separator + "resources", "script.rng"), null,true,false);
		new ScriptHandler(script, transformerHandlers, getEventListeners(), validator);
	}
}
