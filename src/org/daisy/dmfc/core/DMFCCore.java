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
 * dmfc.reloadTransformers();
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
	 * @param a_inputListener
	 * @param a_eventListener
	 */
	public DMFCCore(InputListener a_inputListener, EventListener a_eventListener) {
	    this(a_inputListener, a_eventListener, new Locale("en"));
	}
	
	/**
	 * Create an instance of DMFC.
	 * @param a_inputListener a listener of (user) input events
	 * @param a_eventListener a listener of events
	 * @param a_locale the locale to use
	 */
	public DMFCCore(InputListener a_inputListener, EventListener a_eventListener, Locale a_locale) {
		super(a_eventListener);
		inputListener = a_inputListener;
		Locale.setDefault(a_locale);
		
		// Set DMFC home dir
		home = getHomeDir();
		System.setProperty("dmfc.home", home);				
		
		// Load properties
		if (!loadProperties(ClassLoader.getSystemResourceAsStream("dmfc.properties"))) {
		    System.err.println("Can't read properties!");
		}		
		
		// Load messages
		DirClassLoader _resourceLoader = new DirClassLoader(new File(home, "resources"), new File(home, "resources"));
		ResourceBundle _bundle = ResourceBundle.getBundle("dmfc_messages", Locale.ENGLISH, _resourceLoader);
		I18n.setDefaultBundle(_bundle);				
		
		TempFile.setTempDir(new File(System.getProperty("dmfc.tempDir")));
			
		// Setup logging
		MessageLogger _logger = new MessageLogger();
		addEventListener(_logger);
		LoggingPropertiesReader.addHandlers(_logger, System.getProperty("dmfc.logging"));		
	}	

	/**
	 * Find out what the home directory of DMFC is.
	 * @return the home directory of DMFC.
	 */
	private String getHomeDir() {
	    URL _url = ClassLoader.getSystemResource("dmfc.properties");
	    if (_url == null) {
	        System.err.println("Can't find dmfc.properties");
	        // FIXME throw Exception
	    }
	    String _dir = new File(_url.getFile()).getParentFile().getParent();
	    try {	        
            _dir = URLDecoder.decode(_dir, "utf-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("This is not supposed to happen!");
        }
	    return _dir;
	}
	
	/**
	 * Adds a set properties to the system properties.
	 * @param a_propertiesStream an InputStream
	 * @return <code>true</code> if the loading was successful, <code>false</code> otherwise
	 */
	public boolean loadProperties(InputStream a_propertiesStream) {
	    try {
	        Properties _properties = new Properties(System.getProperties());
            _properties.load(a_propertiesStream);         
            System.setProperties(_properties);
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
			Validator _validator = new RelaxngSchematronValidator(new File(home + File.separator + "resources", "transformer.rng"), true);
			sendMessage(Level.CONFIG, "Reloading Transformers");
			transformerHandlers.clear();		
			addTransformers(new File(home, "transformers"), _validator);			
			sendMessage(Level.CONFIG, "Reloading of Transformers done");
		} catch (ValidationException e) {
			sendMessage(Level.SEVERE, "Reloading of Transformers failed " + e.getMessage());
			return false;
		}		
		return true;
	}
	
	/**
	 * Recursively add transformers as the transformer description files (TDFs) are found
	 * @param a_dir the directory to start searching in
	 * @param a_validator a Validator of TDFs
	 */
	private void addTransformers(File a_dir, Validator a_validator) {
		if (!a_dir.isDirectory()) {
			sendMessage(Level.SEVERE, a_dir.getAbsolutePath() + " is not a directory.");
			return;
		}
		File[] _children = a_dir.listFiles();
		for (int i = 0; i < _children.length; ++i) {
			File _current = _children[i];
			if (_current.isDirectory()) {
				addTransformers(_current, a_validator);
			}
			else if (_current.getName().matches(".*\\.tdf")) {
				try {
					TransformerHandler _th = new TransformerHandler(_current, inputListener, getEventListeners(), a_validator);
					if (transformerHandlers.containsKey(_th.getName())) {
						throw new TransformerDisabledException("Transformer '" + _th.getName() + "' aleady exists");
					}
					transformerHandlers.put(_th.getName(), _th);
				} catch (TransformerDisabledException e) {
					sendMessage(Level.WARNING, "Transformer in file '" + _current.getAbsolutePath() + "' disabled: " + e.getMessage());
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
	 * @param a_script the script to execute
	 * @return true if the exeution was successful, false otherwise.
	 */
	public boolean executeScript(File a_script) {		
		boolean _ret = false;
		try {
			Validator _validator = new RelaxngSchematronValidator(new File(home + File.separator + "resources", "script.rng"), false);
			ScriptHandler _handler = new ScriptHandler(a_script, transformerHandlers, getEventListeners(), _validator);
			_handler.execute();
			_ret = true;
		} catch (ScriptException e) {
			sendMessage(Level.SEVERE, "Script file exception: " + e.getMessage());
			if (e.getRootCause() != null) {
			    String _msg = new String();
			    String[] _msgs = e.getRootCauseMessages();			    
			    for (int i = 0; i < _msgs.length; ++i) {
			        _msg = _msgs[i] + "\n";
			    }
				sendMessage(Level.SEVERE, "Root cause: " + _msg);				
			}
		} catch (ValidationException e) {
			sendMessage(Level.SEVERE, "Problems parsing script file" + e.getMessage());
			if (e.getRootCause() != null) {
				sendMessage(Level.SEVERE, "Root cause: " + e.getRootCause().getMessage());
			}
		} catch (MIMEException e) {
			if (e.getRootCause() != null) {
			    String _msg = new String();
			    String[] _msgs = e.getRootCauseMessages();			    
			    for (int i = 0; i < _msgs.length; ++i) {
			        _msg = _msgs[i] + "\n";
			    }
				sendMessage(Level.SEVERE, "Root cause: " + _msg);				
			}
        }
		return _ret;
	}
	
	/**
	 * Validates a task script
	 * @param a_script the script to validate
	 * @throws ValidationException
	 * @throws ScriptException
	 * @throws MIMEException
	 */
	public void validateScript(File a_script) throws ValidationException, ScriptException, MIMEException {
		Validator _validator = new RelaxngSchematronValidator(new File(home + File.separator + "resources", "script.rng"), false);
		new ScriptHandler(a_script, transformerHandlers, getEventListeners(), _validator);
	}
}
