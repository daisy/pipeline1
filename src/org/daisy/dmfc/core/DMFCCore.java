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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.exception.TransformerDisabledException;
import org.daisy.dmfc.logging.LoggingPropertiesReader;
import org.daisy.dmfc.logging.MessageLogger;
import org.daisy.util.exception.ValidationException;
import org.daisy.util.file.TempFile;
import org.daisy.util.i18n.I18n;
import org.daisy.util.xml.validator.RelaxngSchematronValidator;
import org.daisy.util.xml.validator.Validator;


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

	private InputListener inputListener;
	private Map transformerHandlers = new HashMap();
	private String home;
	
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
		
		// Set DMFC home dir
		home = getHomeDir();
		System.setProperty("dmfc.home", home);
		
		// Load properties from file
		if (!loadProperties(ClassLoader.getSystemResourceAsStream("dmfc.properties"))) {
		    throw new DMFCConfigurationException("Can't read dmfc.properties!");
		}
		
		// Load messages
		DirClassLoader resourceLoader = new DirClassLoader(new File(home, "resources"), new File(home, "resources"));
		ResourceBundle bundle = ResourceBundle.getBundle("dmfc_messages", Locale.ENGLISH, resourceLoader);
		I18n.setDefaultBundle(bundle);				
		
		TempFile.setTempDir(new File(System.getProperty("dmfc.tempDir")));
			
		// Setup logging
		MessageLogger logger = new MessageLogger();
		addEventListener(logger);
		LoggingPropertiesReader.addHandlers(logger, System.getProperty("dmfc.logging"));
		
		// Load the transformers
		if (!reloadTransformers()) {
		    throw new DMFCConfigurationException("Cannot load the transformers");
		}
	}	

	/**
	 * Find out what the home directory of DMFC is.
	 * @return the home directory of DMFC.
	 * @throws DMFCConfigurationException
	 */
	private String getHomeDir() throws DMFCConfigurationException {
	    URL url = ClassLoader.getSystemResource("dmfc.properties");
	    if (url == null) {
	        System.err.println("Can't find dmfc.properties");
	        throw new DMFCConfigurationException("Can't find dmfc.properties");
	    }
	    String dir = new File(url.getFile()).getParentFile().getParent();
	    try {	        
            dir = URLDecoder.decode(dir, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new DMFCConfigurationException("This is not supposed to happen!", e);            
        }
	    return dir;
	}
	
	/**
	 * Adds a set properties to the system properties.
	 * @param a_propertiesStream an InputStream
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
			Validator validator = new RelaxngSchematronValidator(new File(home + File.separator + "resources", "transformer.rng"), true);
			sendMessage(Level.CONFIG, "Reloading Transformers");
			transformerHandlers.clear();		
			addTransformers(new File(home, "transformers"), validator);			
			sendMessage(Level.CONFIG, "Reloading of Transformers done");
		} catch (ValidationException e) {
			sendMessage(Level.SEVERE, "Reloading of Transformers failed " + e.getMessage());
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
			sendMessage(Level.SEVERE, dir.getAbsolutePath() + " is not a directory.");
			return;
		}
		File[] children = dir.listFiles();
		for (int i = 0; i < children.length; ++i) {
			File current = children[i];
			if (current.isDirectory()) {
				addTransformers(current, validator);
			}
			else if (current.getName().matches(".*\\.tdf")) {
				try {
					TransformerHandler th = new TransformerHandler(current, inputListener, getEventListeners(), validator);
					if (transformerHandlers.containsKey(th.getName())) {
						throw new TransformerDisabledException("Transformer '" + th.getName() + "' aleady exists");
					}
					transformerHandlers.put(th.getName(), th);
				} catch (TransformerDisabledException e) {
					sendMessage(Level.WARNING, "Transformer in file '" + current.getAbsolutePath() + "' disabled: " + e.getMessage());
					if (e.getRootCause() != null) {
						sendMessage(Level.WARNING, "Root cause: " + e.getRootCauseMessagesAsString());
					}
				}
			}
		}
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
			Validator validator = new RelaxngSchematronValidator(new File(home + File.separator + "resources", "script.rng"), false);
			ScriptHandler handler = new ScriptHandler(script, transformerHandlers, getEventListeners(), validator);
			handler.execute();
			ret = true;
		} catch (ScriptException e) {
			sendMessage(Level.SEVERE, "Script file exception: " + e.getMessage());
			if (e.getRootCause() != null) {
			    String msg = "";
			    String[] msgs = e.getRootCauseMessages();			    
			    for (int i = 0; i < msgs.length; ++i) {
			        msg = msgs[i] + "\n";
			    }
				sendMessage(Level.SEVERE, "Root cause: " + msg);				
			}
		} catch (ValidationException e) {
			sendMessage(Level.SEVERE, "Problems parsing script file" + e.getMessage());
			if (e.getRootCause() != null) {
				sendMessage(Level.SEVERE, "Root cause: " + e.getRootCause().getMessage());
			}
		} catch (MIMEException e) {
			if (e.getRootCause() != null) {
			    String msg = "";
			    String[] msgs = e.getRootCauseMessages();			    
			    for (int i = 0; i < msgs.length; ++i) {
			        msg = msgs[i] + "\n";
			    }
				sendMessage(Level.SEVERE, "Root cause: " + msg);				
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
		Validator validator = new RelaxngSchematronValidator(new File(home + File.separator + "resources", "script.rng"), false);
		new ScriptHandler(script, transformerHandlers, getEventListeners(), validator);
	}
}
