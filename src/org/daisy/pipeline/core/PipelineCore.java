/*
 * Daisy Pipeline (C) 2005-2008 Daisy Consortium
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
package org.daisy.pipeline.core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import org.daisy.pipeline.core.script.Creator;
import org.daisy.pipeline.core.script.Job;
import org.daisy.pipeline.core.script.Runner;
import org.daisy.pipeline.core.script.Script;
import org.daisy.pipeline.core.script.ScriptValidationException;
import org.daisy.pipeline.core.transformer.TransformerHandlerLoader;
import org.daisy.pipeline.exception.DMFCConfigurationException;
import org.daisy.pipeline.exception.JobFailedException;
import org.daisy.util.file.TempFile;
import org.daisy.util.i18n.I18n;
import org.daisy.util.i18n.XMLProperties;
import org.daisy.util.i18n.XMLPropertyResourceBundle;

/**
 * This is the class users of DMFC should instantiate. A common usage of DMFC
 * would include the following:
 * 
 * <pre>
 * DMFCCore dmfc = new DMFCCore(inputListener, locale);
 * dmfc.executeScript(scriptFile);
 * </pre>
 * 
 * @author Linus Ericson
 */
public class PipelineCore {

    private Creator mCreator;
    private Runner mRunner;

    /**
     * Create an instance of the Daisy Pipeline. This constructor will fetch the
     * Pipeline properties files in the <code>bin/</code> sub-directory of
     * <code>homeDir</code>.
     */
    public PipelineCore() throws DMFCConfigurationException {
        this(null, null, null, null);
    }
    
    /**
     * Create an instance of the Daisy Pipeline. This constructor will fetch the
     * Pipeline properties files in the <code>bin/</code> sub-directory of
     * <code>homeDir</code>.
     * 
     * @param inListener a listener of (user) input events
     */
    public PipelineCore(InputListener inListener)
            throws DMFCConfigurationException {
        this(inListener, null, null, null);
    }
    
    /**
     * Create an instance of the Daisy Pipeline. This constructor will fetch the
     * Pipeline properties files in the <code>bin/</code> sub-directory of
     * <code>homeDir</code>.
     * 
     * @param inListener a listener of (user) input events
     * @param homeDir the home directory
     */
    public PipelineCore(InputListener inListener, File homeDir)
            throws DMFCConfigurationException {
        this(inListener, homeDir, null, null);
    }

    /**
     * Create an instance of the Daisy Pipeline.
     * 
     * @param inListener a listener of (user) input events
     * @param homeDir the home directory
     * @param userProps a set of user properties
     */
    public PipelineCore(InputListener inListener, File homeDir, Properties userProps)
            throws DMFCConfigurationException {
    	this(inListener, homeDir, userProps, null);
    }
    
    /**
     * Creates an instance of Daisy Pipeline. This constructor gives
     * the user the opportunity to supply both user properties and
     * pipeline properties as <code>userProps</code> and 
     * <code>pipelineProps</code>.
     * 
     * @param inListener a listener of (user) input events
     * @param homeDir the directory considered the daisy pipeline home directory
     * @param userProps a set of user properties
     * @param pipelineProps a set of pipeline properties
     * @throws DMFCConfigurationException it the pipeline properties cannot
     * be read.
     */
    public PipelineCore(InputListener inListener, File homeDir, Properties userProps, Properties pipelineProps) 
    		throws DMFCConfigurationException {
         homeDir = (homeDir!=null)?homeDir:findHomeDirectory();
         TransformerHandlerLoader.INSTANCE.setInputListener(inListener); 
         TransformerHandlerLoader.INSTANCE.setTransformersDirectory(new File(homeDir, "transformers")); 
         mCreator = new Creator();
         mRunner = new Runner();
         initialize(userProps, pipelineProps);
    }

    private void initialize(Properties userProps, Properties pipelineProps)
            throws DMFCConfigurationException, SecurityException {
    	
    	// Martin Blomberg 20071109: make it possible to give both
    	// Properties as parameters eventougth pipeline.properties
    	// is not normally accessed by users.
    	if (pipelineProps == null) {
        	// Load properties
            // mg 20070530: we use two properties files; one with likelihood of user
            // access and one less likely
            // Init system properties
            URL propsURL = getClass().getClassLoader().getResource(
                    "pipeline.properties");
            XMLProperties properties = new XMLProperties(System.getProperties());
            try {
                properties.loadFromXML(propsURL.openStream());
            } catch (IOException e) {
                throw new DMFCConfigurationException(
                        "Can't read pipeline.properties", e);
            }
            System.setProperties(properties);
    	} else {
       		System.getProperties().putAll(pipelineProps);
    	}

    	// Init user properties
    	if (userProps == null) {
			URL propsURL = getClass().getClassLoader().getResource(
					"pipeline.user.properties");
			userProps = new XMLProperties();
			try {
				userProps.loadFromXML(propsURL.openStream());
			} catch (IOException e) {
				throw new DMFCConfigurationException(
						"Can't read pipeline.properties", e);
			}
		}
        setUserProperties(userProps);

        // Load messages
        ResourceBundle bundle = XMLPropertyResourceBundle.getBundle((this
                .getClass().getPackage().getName()).replace('.', '/')
                + "/messages.properties", Locale.ENGLISH, this.getClass()
                .getClassLoader());
        // ResourceBundle bundle =
        // XMLPropertyResourceBundle.getBundle(this.getClass().getPackage().getName()
        // + ".pipeline.messages", Locale.getDefault(),
        // this.getClass().getClassLoader());
        // alternatively:
        // ResourceBundle bundle = XMLPropertyResourceBundle.getBundle(
        // this.getClass().getResource("pipeline.messages"), Locale.ENGLISH);

        I18n.setDefaultBundle(bundle);
        
        //Setup Logging Properties
        if (System.getProperty("java.util.logging.config.file") == null) {
        	System.setProperty("java.util.logging.config.file", "logging.properties");
        }
    }
    /**
     * Configure the Pipeline with the given user properties. This
     * implementation adds the given properties to the System properties.
     * 
     * @param properties the user properties used to configure the Pipeline
     */
    public void setUserProperties(Properties properties) {
    	if (properties == null) {
			throw new IllegalArgumentException("properties can't be null");
		}
        // Set system properties
        for (Object key : properties.keySet()) {
            String name = (String) key;
            System.setProperty(name, properties.getProperty(name));
        }
        // Apply new properties if possible
        TempFile.setTempDir(new File(System.getProperty("pipeline.tempDir")));
    }

    /**
     * Creates a new Script object from a script file
     * 
     * @param url
     * @return a Script
     * @throws ScriptValidationException
     */
    public Script newScript(URL url) throws ScriptValidationException {
        return mCreator.newScript(url);
    }

    /**
     * Execute a script contained in a ScriptRunner object.
     * 
     * @param job
     * @throws JobFailedException
     */
    public void execute(Job job) throws JobFailedException {
        this.mRunner.execute(job);
    }

    /**
     * Gets the number of completed tasks in the current script. If no script is
     * currently being executed, 0 is returned.
     * 
     * @return the number of completed tasks
     */
    public int getCompletedTasks() {
        return mRunner.getCompletedTasks();
    }

    /**
     * Is a script currently being run?
     * 
     * @return true if a script is currently running, false otherwise
     */
    public boolean isRunning() {
        return mRunner.isRunning();
    }


    /**
     * Finds the pipeline home directory.
     * 
     * @param propertiesURL
     * @return
     * @throws DMFCConfigurationException
     */
    public static File findHomeDirectory() throws DMFCConfigurationException {
        URL propertiesURL = PipelineCore.class.getClassLoader().getResource(
                "pipeline.properties");
        File propertiesFile = null;
        try {
            propertiesFile = new File(propertiesURL.toURI());
        } catch (URISyntaxException e) {
            throw new DMFCConfigurationException(e.getMessage(), e);
        }
        // Is this the home dir?
        File folder = propertiesFile.getParentFile();
        if (PipelineCore.testHomeDirectory(folder)) {
            return folder;
        }
        // Test parent
        folder = folder.getParentFile();
        if (PipelineCore.testHomeDirectory(folder)) {
            return folder;
        }
        throw new DMFCConfigurationException(
                "Cannot locate the Daisy Pipeline home directory");
    }
    
    /**
     * Tests if a given directory is the home directory.
     * 
     * @param folder a possible home directory
     * @return true if the given folder is the home directory, false otherwise
     */
    public static boolean testHomeDirectory(File folder) {
        File[] files = folder.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return ("transformers".equals(file.getName()) && file
                        .isDirectory());
            }
        });
        return files != null && files.length == 1;
    }

}
