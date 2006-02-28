/*
 * Created on 2005-mar-08
 */
package org.daisy.dmfc.core.transformer;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;

import org.daisy.dmfc.core.EventSender;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.Prompt;
import org.daisy.dmfc.exception.TransformerRunException;

/**
 * Base class for all Transformers. Every Transformer extending this base class
 * must provide a constructor taking the same parameters as the constructor of
 * this base class does.
 * 
 * Extending this class shall not be considered as derivative works.
 * 
 * @author Linus Ericson
 */
public abstract class Transformer extends EventSender {
	
	private boolean interactive;	
	private InputListener inputListener;
    
    private long startTime = 0;
	
	private File transformerDirectory = null;
	
	/**
	 * Creates a new Transformer.
	 * @param eventListeners a set of  event listeners
	 * @param isInteractive
	 */
	public Transformer(InputListener inListener, Set eventListeners, Boolean isInteractive) {		
		super(eventListeners);
		inputListener = inListener;
		interactive = isInteractive.booleanValue();
		messageOriginator = "Transformer";
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.ENGLISH, this.getClass().getClassLoader());
			addI18nBundle(bundle);
		} catch (MissingResourceException e) {
			sendMessage(Level.INFO, "No resource bundle found for " + this.getClass().getName());
		}
	}
	
	/*package*/ final void setMessageOriginator(String originator) {
	    messageOriginator = originator;
	}
	
	/**
	 * Executes the Transformer with the specified parameters.
	 * @param parameters a collection of parameters
	 * @return <code>true</code> if the Transformer was successful, <code>false</code> otherwise.
	 */
	protected abstract boolean execute(Map parameters) throws TransformerRunException;
	
	final public boolean executeWrapper(Map parameters, File dir) throws TransformerRunException {
	    boolean ret;
	    transformerDirectory = dir;
	    status(true);
        startTime = System.currentTimeMillis();
	    ret = execute(parameters);
	    status(false);
	    return ret;
	}
	
    /**
     * Sends a progress report to all listeners.
     * @param progress the progress
     */
    protected void progress(double progress) {
        Prompt prompt = new Prompt(progress, startTime, messageOriginator);
        send(prompt);
    }
    
    
	/**
	 * Performs any Transformer specific checks. In the default implementation,
	 * this function always returns <code>true</code>.
	 * @return <code>true</code> if al dependency ckecks are OK, <code>false</code> otherwise
	 */
	public static boolean isSupported() {
		return true;
	}
	
	/**
	 * Final function for reading user input. This method cannot be overridden.
	 * @param level the level of the message
     * @param the message itself
	 * @param defaultValue a default value
	 * @return the input from the user if the Transformer was run in interactive mode, the default value otherwise.
	 */
	final protected String getUserInput(Level level, String message, String defaultValue) {
		if (!interactive) {
		    return defaultValue;			
		}		
		return inputListener.getInputAsString(new Prompt(level, message, messageOriginator));		
	}
	
	/**
	 * Final function for returning the transformer directory. This method cannot be overridden.
	 * @return the directory of the transformer
	 */
	final protected File getTransformerDirectory() {
	    return transformerDirectory;
	}
}
