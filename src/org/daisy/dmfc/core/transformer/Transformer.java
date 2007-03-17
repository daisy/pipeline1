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
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import org.daisy.dmfc.core.EventSender;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.Prompt;
import org.daisy.dmfc.core.listener.TransformerProgressListener;
import org.daisy.dmfc.exception.TransformerAbortException;
import org.daisy.dmfc.exception.TransformerRunException;
import org.daisy.util.execution.AbortListener;

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
	private Vector abortListeners = new Vector();
	
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
	    if (inputListener.isAborted()) {
	        throw new TransformerAbortException(messageOriginator + " aborted.");
	    }
	    boolean ret;
	    transformerDirectory = dir;
	    //status(true);
	    //mg 20070316: instead of deprecated status(bool):
	    sendTransformerStatusMessage(true);
	    this.progress(0);
        startTime = System.currentTimeMillis();
	    ret = execute(parameters);
	    if (inputListener.isAborted()) {
	        throw new TransformerAbortException(messageOriginator + " aborted.");
	    }
	    this.progress(1);
	    //status(false);
	    //mg 20070316: instead of deprecated status(bool):
	    sendTransformerStatusMessage(false);
	    return ret;
	}
	
    private void sendTransformerStatusMessage(boolean running) {
		for (Iterator iter = this.getEventListeners().iterator(); iter.hasNext();) {
			Object listener = iter.next();
			if(listener instanceof TransformerProgressListener) {
				if(running) {
					((TransformerProgressListener)listener).transformerStart(this);
				}else{
					((TransformerProgressListener)listener).transformerEnd(this);
				}
			}
			
		}		
	}

	/**
     * Sends a progress report to all listeners.
     * @param progress the progress
     */
    protected void progress(double progress) {
		for (Iterator iter = this.getEventListeners().iterator(); iter.hasNext();) {
			Object listener = iter.next();
			if(listener instanceof TransformerProgressListener) {				
				((TransformerProgressListener)listener).transformerProgress(progress, this);				
			}else if(listener instanceof org.daisy.dmfc.core.EventListener) {
		        Prompt prompt = new Prompt(progress, startTime, messageOriginator);
		        ((org.daisy.dmfc.core.EventListener)listener).message(prompt);		
			}			
		}
    }
    
    protected void abortEvent() {
        // Nothing by default
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
     * @param message the message itself
	 * @param defaultValue a default value
	 * @return the input from the user if the Transformer was run in interactive mode, the default value otherwise.
	 */
	final protected String getUserInput(Level level, String message, String defaultValue) {
		if (!interactive) {
		    return defaultValue;			
		}		
		return inputListener.getInputAsString(new Prompt(level, message, messageOriginator));		
	}
	
	final protected void checkAbort() throws TransformerAbortException {
	    if (inputListener.isAborted()) {
	    	for (int i = abortListeners.size() - 1; i >= 0; i--) {
	    		AbortListener abort = (AbortListener) abortListeners.get(i);
	    		abort.abortEvent();
	    	}
	        abortEvent();
	        throw new TransformerAbortException(messageOriginator + " aborted.");
	    }
	}
	
	
	/**
	 * Adds an <code>AbortListener</code> to the set of listeners for this transformer, 
	 * provided that it is not the same as some listener already in the set. 
	 * The abort events will be delivered in a LIFO order, i e 
	 * the first listener to be added will be notified last, and vice versa.
	 * 
	 * @param listener a listener to be added.
	 */
	public void addAbortListener(AbortListener listener) {
		if (!abortListeners.contains(listener)) {
			abortListeners.add(listener);
		}
	}
	
	
	/**
	 * Removes an abort listener from the set of abort listeners of this transformer. 
	 * @param listener a listener to be removed.
	 */
	public void removeAbortListener(AbortListener listener) {
		abortListeners.remove(listener);
	}
	
	/**
	 * Final function for returning the transformer directory. This method cannot be overridden.
	 * @return the directory of the transformer
	 */
	final protected File getTransformerDirectory() {
	    return transformerDirectory;
	}
}
