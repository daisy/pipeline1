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
	Prompt prompt;
	TableViewer tableViwer;
	Job currentJob;
	
	
	public void message(Prompt _prompt) {
		this.prompt=_prompt;
		
	    if (prompt.getType() == Prompt.MESSAGE && prompt.getLevel().intValue() >= Level.WARNING.intValue()) {
	        System.out.println("[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage());
	        //message="[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage(); 
	        message="[" + prompt.getMessage() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessageOriginator(); 
	        appendMessageToFile(message);
	    }
	    if (prompt.getType() == Prompt.TRANSFORMER_START ) {
	       //System.out.println("Transformer " + prompt.getMessageOriginator() + " has just been started");
	        message="Transformer " + prompt.getMessageOriginator() + " has just been started";
	        appendMessageToFile(message);
	    
	    }
	    if (prompt.getType() == Prompt.TRANSFORMER_END ) {
	       // System.out.println("Transformer " + prompt.getMessageOriginator() + " has just finished running");
	        message="Transformer " + prompt.getMessageOriginator() + " has just finished running";
	        appendMessageToFile(message);
	        
	        // A transformer has finished. Check it off in the table.
	        UIManager.display.syncExec(new Runnable(){
				public void run(){
					tableViwer.getTable().getItem(currentJob.getScript().getCurrentTaskIndex()).setChecked(true);
				}
			});	        
	    }
	    if (prompt.getType() ==Prompt.PROGRESS ){
	    	calculateTiming(prompt);
	    }
	}
	
	public void calculateTiming(Prompt prompt){
		
		UIManager.display.syncExec(new Runnable(){
			public void run(){
				
				setTimes();
				
				if (timeLeft!=null){
					estimated.setText(timeLeft.toString(8));
					
				}
				if (totalTime!=null){
					elapsed.setText(totalTime.toString(8));
					
				}
				progBar.setSelection((int)(progress * 100));
			}
		});
		
		messageOriginator = prompt.getMessageOriginator();
		type = prompt.getType();
		message = prompt.getMessage();
	}
	
	public void setTimes(){
		this.timeLeft=prompt.getTimeLeft();	
		this.totalTime=prompt.getTotalTime();
		progress = prompt.getProgress();
	}
	
	
	public void setAttributes(Text elapsed, Text estimated,  
			ProgressBar _pb, TableViewer tv){
		this.elapsed=elapsed;
		this.estimated= estimated;
		this.progBar=_pb;
		this.tableViwer = tv;
	}
	
	public void setJob(Job job) {
		this.currentJob = job;
	}	

	public String getMessage(){
		return this.message;
	}
		
	
	public int getType(){
		return this.type;
	}
	
	public String getMessageOriginator(){
		return this.messageOriginator;
	}
	
	
	/**
	 * 
	 * @param message
	 */
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
