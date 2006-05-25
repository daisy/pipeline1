package org.daisy.dmfc.gui.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.Prompt;
import org.daisy.dmfc.gui.UIManager;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.util.xml.SmilClock;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

public class LocalEventListener implements EventListener{
	
	String message;
	SmilClock timeLeft;
	SmilClock totalTime;
	double progress;
	String messageOriginator;
	int type;
	ProgressBar progBar;
	Text elapsed;
	Text estimated;
	
	
	public void message(Prompt prompt) {
	    if (prompt.getType() == Prompt.MESSAGE && prompt.getLevel().intValue() >= Level.ALL.intValue()) {
	        System.out.println("[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage());
	        message="[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage();
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
		progress = prompt.getProgress();
		
		
		UIManager.display.syncExec(new Runnable(){
			public void run(){
				
				if (timeLeft!=null){
					elapsed.setText(new Double(getTimeLeft()).toString());
				}
				if (totalTime!=null){
					elapsed.setText(new Double(getTotalTime()).toString());
				}
				progBar.setSelection((int)(progress * 100));
			}
		});
		
		messageOriginator = prompt.getMessageOriginator();
		type = prompt.getType();
		message = prompt.getMessage();
	}
	
	public void setAttributes(Text elapsed, Text estimated,  
			ProgressBar _pb){
		this.elapsed=elapsed;
		this.estimated= estimated;
		this.progBar=_pb;
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
	
	public void appendMessageToFile(String message){
		String logFileName = System.getProperty("user.dir")+ File.separator + "logFile.txt";
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(logFileName, true));
			out.write("\n");
			out.write(message);
			out.close();
		} 
		catch (IOException e) {
		}
	}
}
