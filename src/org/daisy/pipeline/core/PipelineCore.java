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
import org.daisy.util.runtime.RegistryQuery;

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

    protected Creator mCreator;
    private Runner mRunner;
    private File mHomeDir;

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
         mHomeDir = (homeDir!=null)?homeDir:findHomeDirectory();
         TransformerHandlerLoader.INSTANCE.setInputListener(inListener); 
         TransformerHandlerLoader.INSTANCE.setTransformersDirectory(new File(mHomeDir, "transformers")); 
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
        ResourceBundle bundle = XMLPropertyResourceBundle.getBundle(
            "org/daisy/pipeline/core/messages.properties", Locale.getDefault(),
            this.getClass().getClassLoader());
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
            if ("pipeline.lame.path".equals(name)) {
                String lamePath = initLamePath(properties.getProperty(name));
                System.setProperty(name, lamePath);
            } else {
                System.setProperty(name, properties.getProperty(name));
            }
        }
        // Apply new properties if possible
        TempFile.setTempDir(new File(System.getProperty("pipeline.tempDir")));
    }
    
    /**
     * Initializes the Lame path. If the <code>pipline.lame.path</code> property is
     * undefined (or set to the empty string), the Lame path can be autodetected on
     * Windows if Lame has been installed using the NSIS installer. 
     * If Lame is found in both HCLM and HKCU (i.e. installed both by the system
     * administrator and the current user), the one with the latest version
     * number is used.
     * @param propertyValue the <code>pipeline.lame.path</code> property value
     * @return the (possibly autodetected) lame path
     */
    public static String initLamePath(String propertyValue) {
        // If the pipeline.lame.path system property is undifined (or set to the empty string),
        // and we are on the Windows platform, try to detect Lame using the registry.
        if ((propertyValue == null || "".equals(propertyValue)) && System.getProperty("os.name").matches("Windows.*")) {
            boolean found = false;
            // We look in both HKLM and HKCU
            String hklmPath = RegistryQuery.readString("HKLM\\Software\\Lame", "Path");
            String hklmVersion = RegistryQuery.readString("HKLM\\Software\\Lame", "Version");
            String hkcuPath = RegistryQuery.readString("HKCU\\Software\\Lame", "Path");
            String hkcuVersion = RegistryQuery.readString("HKCU\\Software\\Lame", "Version");
            if (hklmPath != null) {
                File lameExe = new File(hklmPath, "lame.exe");
                if (lameExe.exists()) {
                    // Lame found in HKLM
                    propertyValue = lameExe.getAbsolutePath();
                    found = true;
                    if (hkcuPath != null) {
                        lameExe = new File(hkcuPath, "lame.exe");
                        // If Lame is found in HKCU as well, we use the one with the latest
                        // version number.
                        if (lameExe.exists() && hkcuVersion != null) {
                            if (hklmVersion == null || hkcuVersion.compareTo(hklmVersion) >= 0) {                                                        
                                propertyValue = lameExe.getAbsolutePath();
                            }
                        }                        
                    }
                }                
            } 
            if (!found && hkcuPath != null) {
                File lameExe = new File(hkcuPath, "lame.exe");
                if (lameExe.exists()) {
                    // Lame found in HKCU
                    propertyValue = lameExe.getAbsolutePath();
                }
            }
        }
        //System.err.println("Lame path: " + propertyValue);
        return propertyValue;
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
     * Returns the home directory of this Pipeline instance.
     * @return the home directory of this Pipeline instance.
     */
    public File getHomeDirectory() {
    	return mHomeDir;
    }


    /**
     * Finds the pipeline home directory.
     * 
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
