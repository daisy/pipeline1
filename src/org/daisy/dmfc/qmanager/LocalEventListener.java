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
	double progress;
	String messageOriginator;
	int type;
	
	
	public void message(Prompt prompt) {
	    if (prompt.getType() == Prompt.MESSAGE && prompt.getLevel().intValue() >= Level.ALL.intValue()) {
	        System.out.println("[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage());
	        message="[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage();
	       // System.out.println("The prompt message is: " + message);
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
	public void calculateTiming(Prompt prompt){
		
		timeLeft=prompt.getTimeLeft();
		totalTime =prompt.getTotalTime();
		progress=prompt.getProgress();
		messageOriginator = prompt.getMessageOriginator();
		type = prompt.getType();
		message = prompt.getMessage();
		
		
	}

	public String getMessage(){
		return this.message;
	}
	
	
	public double getTimeLeft(){
	
		if (this.timeLeft==null){
			return 0.0;
		}
		else{
			return timeLeft.secondsValue();
		}
	}
	
	public double getTotalTime(){
		if (this.totalTime==null){
			return 0.0;
		}
		else{
			return totalTime.secondsValue();
		}
	}
	
	/**
	 * 
	 * @return int - used for a progress bar
	 */
	public double getProgress(){
		
		return this.progress;
	}
	
	
	public String getTransformerRunning(){
		return this.messageOriginator;
	}
	
	public int getType(){
		return this.type;
	}
	
	public String getMessageOriginator(){
		return this.messageOriginator;
	}
	
	/*
	 *public static final int MESSAGE = 0;
    public static final int PROGRESS = 1;
    public static final int TRANSFORMER_START = 2;
    public static final int TRANSFORMER_END = 3;
	 */
	
}
