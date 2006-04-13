package org.daisy.dmfc.qmanager;

import java.util.HashMap;
import java.util.logging.Level;

import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.Prompt;
import org.daisy.util.xml.SmilClock;

public class LocalEventListener implements EventListener{

	
	String message;
	SmilClock timeLeft;
	SmilClock totalTime;
	
	
	public void message(Prompt prompt) {
	    if (prompt.getType() == Prompt.MESSAGE && prompt.getLevel().intValue() >= Level.ALL.intValue()) {
	        System.out.println("[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage());
	        message="[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage();
	        System.out.println("The prompt message is: " + message);
	    }
	    if (prompt.getType() == Prompt.TRANSFORMER_START) {
	        System.out.println("Transformer " + prompt.getMessageOriginator() + " has just been started");
	        message="Transformer " + prompt.getMessageOriginator() + " has just been started";
	    
	    }
	    if (prompt.getType() == Prompt.TRANSFORMER_END) {
	        System.out.println("Transformer " + prompt.getMessageOriginator() + " has just finished running");
	        message="Transformer " + prompt.getMessageOriginator() + " has just finished running";
	    }
	    if (prompt.getType() ==Prompt.PROGRESS){
	    	calculateTiming(prompt);
	    }
	}
	public HashMap calculateTiming(Prompt prompt){
		System.out.println("I'm calculating the timing...");
		HashMap hm = new HashMap();
		timeLeft=prompt.getTimeLeft();
		totalTime =prompt.getTotalTime();
		hm.put("left", prompt.getTimeLeft());
		hm.put("progress", prompt.getTotalTime());
		return hm;	
	}

	public String getMessage(){
		return this.message;
	}
	
	public double getTimeLeft(){
		return timeLeft.secondsValue();
	}
	public double getTotalTime(){
		return totalTime.secondsValue();
	}
	
	
}
