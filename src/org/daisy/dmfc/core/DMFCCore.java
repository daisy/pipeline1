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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.exception.TransformerDisabledException;
import org.daisy.util.exception.ValidationException;
import org.daisy.util.file.TempFile;
import org.daisy.util.xml.validator.RelaxngSchematronValidator;
import org.daisy.util.xml.validator.Validator;


/**
 * This is the class users of DMFC should instantiate.
 * A common usage of DMFC would include the following:
 * <pre>
 * DMFCCore dmfc = new DMFCCore(inputListener, eventListener);
 * dmfc.reloadTransformers();
 * dmfc.executeScript(file);
 * </pre>
 * @author Linus Ericson
 */
public class DMFCCore extends EventSender {

	private InputListener inputListener;
	private Map transformerHandlers = new HashMap();
	
	/**
	 * Create an instance of DMFC.
	 * @param a_inputListener a listener of (user) input events
	 * @param a_eventListener a listener of events
	 */
	public DMFCCore(InputListener a_inputListener, EventListener a_eventListener) {
		super(a_eventListener);
		inputListener = a_inputListener;
		
		// FIXME no hard coded language please
		DirClassLoader _resourceLoader = new DirClassLoader(new File("resources"), new File("resources"));
		ResourceBundle _bundle = ResourceBundle.getBundle("dmfc_messages", new Locale("sv", "SE"), _resourceLoader);
		setI18nBundle(_bundle);
		
		// FIXME read these from file
		System.setProperty("dmfc.tempDir" , "c:\\temp");
		setLanguage("en");
		
		TempFile.setTempDir(new File(System.getProperty("dmfc.tempDir")));
	}	

	/**
	 * Iterate over all Transformer Description Files (TDF) and
	 * load each Transformer.
	 */
	public void reloadTransformers() {
		try {
			Validator _validator = new RelaxngSchematronValidator(new File("resources", "transformer.rng"), true);
			sendMessage("Reloading Transformers");
			transformerHandlers.clear();		
			addTransformers(new File("transformers"), _validator);			
			sendMessage("Reloading of Transformers done");
		} catch (ValidationException e) {
			sendMessage("Reloading of transformers failed " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the language used by DMFC
	 * @param a_language
	 */
	public void setLanguage(String a_language) {
		System.setProperty("dmfc.lang", a_language);
	}
	
	/**
	 * Recursively add transformers as the transformer description files (TDFs) are found
	 * @param a_dir the directory to start searching in
	 * @param a_validator a Validator of TDFs
	 */
	private void addTransformers(File a_dir, Validator a_validator) {
		if (!a_dir.isDirectory()) {
			sendMessage(a_dir.getAbsolutePath() + " is not a directory.");
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
					TransformerHandler _th = new TransformerHandler(_current, getI18n(), inputListener, getEventListeners(), a_validator);
					if (transformerHandlers.containsKey(_th.getName())) {
						throw new TransformerDisabledException("Transformer '" + _th.getName() + "' aleady exists");
					}
					transformerHandlers.put(_th.getName(), _th);
				} catch (TransformerDisabledException e) {
					sendMessage("Transformer in file '" + _current.getAbsolutePath() + "' disabled: " + e.getMessage());
					if (e.getRootCause() != null) {
						sendMessage("Root cause: " + e.getRootCauseMessagesAsString());
					}
				}
			}
		}
	}
	
	/**
	 * Executes a task script.
	 * @param a_script the script to execute
	 * @return true if the exeution was successful, false otherwise.
	 */
	public boolean executeScript(File a_script) {		
		boolean _ret = false;
		try {
			Validator _validator = new RelaxngSchematronValidator(new File("resources", "script.rng"), false);
			ScriptHandler _handler = new ScriptHandler(a_script, transformerHandlers, getEventListeners(), _validator);
			_handler.execute();
			_ret = true;
		} catch (ScriptException e) {
			sendMessage(e.getMessage());
			if (e.getRootCause() != null) {
			    String _msg = new String();
			    String[] _msgs = e.getRootCauseMessages();			    
			    for (int i = 0; i < _msgs.length; ++i) {
			        _msg = _msgs[i] + "\n";
			    }
				sendMessage("Root cause: " + _msg);				
			}
		} catch (ValidationException e) {
			sendMessage("Problems parsing script file" + e.getMessage());
			if (e.getRootCause() != null) {
				sendMessage("Root cause: " + e.getRootCause().getMessage());
			}
		} catch (MIMEException e) {
			if (e.getRootCause() != null) {
			    String _msg = new String();
			    String[] _msgs = e.getRootCauseMessages();			    
			    for (int i = 0; i < _msgs.length; ++i) {
			        _msg = _msgs[i] + "\n";
			    }
				sendMessage("Root cause: " + _msg);				
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
		Validator _validator = new RelaxngSchematronValidator(new File("resources", "script.rng"), false);
		new ScriptHandler(a_script, transformerHandlers, getEventListeners(), _validator);
	}
}
