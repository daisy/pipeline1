package org.daisy.dmfc.qmanager;

import java.util.logging.Level;

import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.Prompt;

public class LocalEventListener implements EventListener{

	public void message(Prompt prompt) {
	    if (prompt.getType() == Prompt.MESSAGE && prompt.getLevel().intValue() >= Level.ALL.intValue()) {
	        System.out.println("[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage());
	    }
	    if (prompt.getType() == Prompt.TRANSFORMER_START) {
	        System.out.println("Transformer " + prompt.getMessageOriginator() + " has just been started");
	    }
	    if (prompt.getType() == Prompt.TRANSFORMER_END) {
	        System.out.println("Transformer " + prompt.getMessageOriginator() + " has just finished running");
	    }
	}

	
}
