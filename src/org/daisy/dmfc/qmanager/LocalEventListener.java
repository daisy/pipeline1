package org.daisy.dmfc.qmanager;

import java.util.HashMap;
import java.util.logging.Level;

import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.Prompt;
import org.daisy.util.fileset.SmilClock;

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
	    if (prompt.getType() ==Prompt.PROGRESS){
	    	calculateTiming(prompt);
	    }
	}
	public HashMap calculateTiming(Prompt prompt){
		HashMap hm = new HashMap();
		hm.put("left", prompt.getTimeLeft());
		hm.put("progress", prompt.getTotalTime());
		return hm;	
	}

	
}
