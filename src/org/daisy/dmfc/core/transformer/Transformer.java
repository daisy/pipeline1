/*
 * Created on 2005-mar-08
 */
package org.daisy.dmfc.core.transformer;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

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
	
	/**
	 * Creates a new Transformer.
	 * @param a_eventListener an event listener
	 * @param a_interactive
	 */
	public Transformer(InputListener a_inputListener, Set a_eventListeners, Boolean a_interactive) {		
		super(a_eventListeners);
		inputListener = a_inputListener;
		interactive = a_interactive.booleanValue();
		
		try {
			String _lang = System.getProperty("dmfc.lang", "en");
			String _country = System.getProperty("dmfc.country");
			Locale _locale;
			if (_country == null) {
				_locale = new Locale(_lang);
			} else {
				_locale = new Locale(_lang, _country);
			}			
			ResourceBundle _bundle = ResourceBundle.getBundle("messages", _locale, this.getClass().getClassLoader());
			setI18nBundle(_bundle);
		} catch (MissingResourceException e) {
			sendMessage("No resource bundle found!");
		}		
	}
	
	/**
	 * Executes the Transformer with the specified parameters.
	 * @param a_parameters a collection of parameters
	 * @return <code>true</code> if the Transformer was successful, <code>false</code> otherwise.
	 */
	public abstract boolean execute(Map a_parameters) throws TransformerRunException;
	
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
	 * @param a_prompt
	 * @param a_defaultValue a default value
	 * @return the input from the user if the Transformer was run in interactive mode, the default value otherwise.
	 */
	final protected String getUserInput(Prompt a_prompt, String a_defaultValue) {		
		if (interactive) {
			inputListener.getInputAsString(a_prompt);
		}		
		return a_defaultValue;
	}
}
