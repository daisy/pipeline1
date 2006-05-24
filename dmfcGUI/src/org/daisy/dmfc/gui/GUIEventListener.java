package org.daisy.dmfc.gui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.Prompt;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.util.xml.SmilClock;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

public class GUIEventListener implements EventListener {
	
	String message;
	SmilClock timeLeft;
	SmilClock totalTime;
	double progress;
	String messageOriginator;
	int type;
	Text txtRunning;
	Text txtElapsed;
	Text txtEstimated;
	ProgressBar pb;
	TableViewer transformerViewer;
	Job job;
	
	
	public GUIEventListener(){
	}
	
	public void setAttributes(Text elapsed, Text estimated,  
			ProgressBar _pb, TableViewer transformerTable, Job _job){
		this.txtElapsed=elapsed;
		this.txtEstimated = estimated;
		this.pb=_pb;
		this.transformerViewer=transformerTable;
		this.job=_job;
		
	}
	
	public void message(Prompt prompt) {
		if (prompt.getType() == Prompt.MESSAGE && prompt.getLevel().intValue() >= Level.ALL.intValue()) {
			//System.out.println("[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage());
			message="[" + prompt.getMessageOriginator() + ", " + prompt.getLevel().getName() + "] " + prompt.getMessage();
			 System.out.println("The prompt message is: " + message);
			 appendMessageToFile(message);
		}
		if (prompt.getType() == Prompt.TRANSFORMER_START) {
			System.out.println("Transformer " + prompt.getMessageOriginator() + " has just been started");
			message="Transformer " + prompt.getMessageOriginator() + " has just been started";
		}
		if (prompt.getType() == Prompt.TRANSFORMER_END) {
			System.out.println("Transformer " + prompt.getMessageOriginator() + " has just finished running");
			message="Transformer " + prompt.getMessageOriginator() + " has just finished running";
			UIManager.display.syncExec(new Runnable(){
				public void run(){
					transformerViewer.getTable().getItem(job.getScript().getCurrentTaskIndex()).setChecked(true);
					transformerViewer.refresh();
				}
			});
		}
		if (prompt.getType() ==Prompt.PROGRESS){
			message ="Received a progress prompt";
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
		
		UIManager.display.syncExec(new Runnable(){
			public void run(){
				txtEstimated.setText(getTimeLeft());
				txtElapsed.setText(getTotalTime());
				pb.setSelection((int)getProgress()*100);
			}
		});
		
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public String getTimeLeft(){
		
		if (this.timeLeft==null){
			return "0";
		}
		else{
			return new Double(timeLeft.secondsValue()).toString();
			//return timeLeft.secondsValue();;
		}
	}
	
	public String getTotalTime(){
		if (this.totalTime==null){
			return "0";
		}
		else{
			return new Double(totalTime.secondsValue()).toString();
			//return totalTime.secondsValue();
		}
	}
	
	/**
	 * 
	 * @return double - used for a progress bar
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
	
	public void appendMessageToFile(String message){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("c:\\temp\\log", true));
			out.write("\n");
			out.write(message);
			out.close();
		} 
		catch (IOException e) {
		}
	}
	
	/*
	 public static final int MESSAGE = 0;
	 public static final int PROGRESS = 1;
	 public static final int TRANSFORMER_START = 2;
	 public static final int TRANSFORMER_END = 3;
	 */
	
}

