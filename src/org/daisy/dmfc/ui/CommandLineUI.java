/*
 * Created on 2005-mar-09
 */
package org.daisy.dmfc.ui;

import java.io.File;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.Prompt;

/**
 * A simple command line UI for starting DMFC.
 * @author LINUSE
 */
public class CommandLineUI implements InputListener, EventListener {

	public String getInputAsString(Prompt a_prompt) {
		System.err.println("Prompt: ");
		return null;
	}

	public void message(String a_message) {
		System.out.println("Message: " + a_message);		
	}

	public static void main(String[] args) {
		if (args.length == 1) {
			CommandLineUI _ui = new CommandLineUI();
			DMFCCore _dmfc = new DMFCCore(_ui, _ui);
			_dmfc.setLanguage("sv");
			_dmfc.reloadTransformers();
			_dmfc.executeScript(new File(args[0]));
		} else {
			System.out.println("Program requires one parameter (a script file name)");
		}
	}
}
