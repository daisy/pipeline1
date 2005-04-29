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
package org.daisy.dmfc.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.logging.Level;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.InputListener;
import org.daisy.dmfc.core.Prompt;

/**
 * A simple command line UI for starting DMFC.
 * @author Linus Ericson
 */
public class CommandLineUI implements InputListener, EventListener {

	public String getInputAsString(Prompt a_prompt) {
		System.err.println("Prompt: " + a_prompt.getMessage());
		BufferedReader _br = new BufferedReader(new InputStreamReader(System.in));
		String _line = null;
		try {
		    _line = _br.readLine();
        } catch (IOException e) {
        }
		return _line;
	}

	public void message(Prompt a_prompt) {
	    if (a_prompt.getType() == Prompt.MESSAGE && a_prompt.getLevel().intValue() >= Level.ALL.intValue()) {
	        System.out.println(a_prompt.getMessageOriginator() + ": " + a_prompt.getLevel().getName() + " " + a_prompt.getMessage());
	    }
	    if (a_prompt.getType() == Prompt.TRANSFORMER_START) {
	        System.out.println("Transformer " + a_prompt.getMessageOriginator() + " has just been started");
	    }
	    if (a_prompt.getType() == Prompt.TRANSFORMER_END) {
	        System.out.println("Transformer " + a_prompt.getMessageOriginator() + " has just finished running");
	    }
	}

	public static void main(String[] args) {
		if (args.length == 1) {
			CommandLineUI _ui = new CommandLineUI();
			DMFCCore _dmfc = new DMFCCore(_ui, _ui, new Locale("sv", "SE"));			
			_dmfc.reloadTransformers();
			_dmfc.executeScript(new File(args[0]));
		} else {
			System.out.println("Program requires one parameter (a script file name)");
		}
	}
}
