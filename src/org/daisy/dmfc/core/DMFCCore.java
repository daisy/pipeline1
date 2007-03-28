/*
 * DMFC - The DAISY Multi Format Converter
 * Copyright (C) 2005-2007  Daisy Consortium
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
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.daisy.dmfc.core.event.CoreMessageEvent;
import org.daisy.dmfc.core.event.EventBus;
import org.daisy.dmfc.core.event.MessageEvent;
import org.daisy.dmfc.core.script.Creator;
import org.daisy.dmfc.core.script.Job;
import org.daisy.dmfc.core.script.Runner;
import org.daisy.dmfc.core.script.Script;
import org.daisy.dmfc.core.script.ScriptValidationException;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.core.transformer.TransformerHandlerLoader;
import org.daisy.dmfc.exception.DMFCConfigurationException;
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
 * DMFCCore dmfc = new DMFCCore(inputListener, locale);
 * dmfc.executeScript(scriptFile);
 * </pre>
 * @author Linus Ericson
 */
public class DMFCCore implements TransformerHandlerLoader {

    private static File homeDirectory = null;
    
	private InputListener inputListener;
	private Map<String,TransformerHandler> transformerHandlers = new HashMap<String, TransformerHandler>();
	
	private Creator mCreator;
	private Runner mRunner;	
	

	/**
	 * Create an instance of the Daisy Pipeline.
	 * @param inListener a listener of (user) input events
	 */

	public DMFCCore(InputListener inListener) throws DMFCConfigurationException {
	    this(inListener, new Locale("en"));
	}

	/**
	 * Create an instance of the Daisy Pipeline.
	 * @param inListener a listener of (user) input events
	 * @param locale the locale to use
	 */
	public DMFCCore(InputListener inListener, Locale locale) throws DMFCConfigurationException {
		super();
		inputListener = inListener;
		initialize(locale);
	}
	
	private void initialize(Locale locale) throws DMFCConfigurationException, SecurityException {
		Locale.setDefault(locale);
		
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
		LoggingPropertiesReader.addHandlers(logger, System.getProperty("dmfc.logging"));
		
		mCreator = new Creator(this);
		mRunner = new Runner();
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
	
	/*
	 * (non-Javadoc)
	 * @see org.daisy.dmfc.core.transformer.TransformerHandlerLoader#getTransformerHandler(java.lang.String)
	 */
	public TransformerHandler getTransformerHandler(String transformerName) throws TransformerDisabledException {
		if (transformerHandlers.containsKey(transformerName)) {
		    return transformerHandlers.get(transformerName);						
		}
		Validator validator;
		try {
			validator = new RelaxngSchematronValidator(this.getClass().getResource("./transformer/transformer-1.1.rng"), null,true,true);
		} catch (ValidationException e) {
			EventBus.getInstance().publish(new CoreMessageEvent(this,"Error! Cannot create TDF validator for transformer " + transformerName,MessageEvent.Type.WARNING));
			return null;
		}
		File[] files = new File(new File(getHomeDirectory(), "transformers"), transformerName).listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".tdf");
			}			
		});
		if (files.length != 1) {
			EventBus.getInstance().publish(new CoreMessageEvent(this,"Error! Incorrect number of TDFs for transformer " + transformerName,MessageEvent.Type.WARNING));
			return null;
		}
		TransformerHandler th = new TransformerHandler(files[0], inputListener, validator);		
		transformerHandlers.put(transformerName, th);		
		return th;
	}
	
	/**
	 * Creates a new Script object from a script file
	 * @param url
	 * @return
	 * @throws ScriptValidationException
	 */
	public Script newScript(URL url) throws ScriptValidationException {
		return mCreator.newScript(url);
	}
	
	/**
	 * Execute a script contained in a ScriptRunner object.
	 * @param job
	 * @throws ScriptException
	 */
	public void execute(Job job) throws ScriptException {		
		this.mRunner.execute(job);		
	}
	
	/**
	 * Gets the number of completed tasks in the current script.
	 * If no script is currently being executed, 0 is returned.
	 * @return
	 */
	public int getCompletedTasks() {
		return mRunner.getCompletedTasks();
	}
	
	/**
	 * Is a script currently being run?
	 * @return
	 */
	public boolean isRunning() {
		return mRunner.isRunning();
	}
	

}
