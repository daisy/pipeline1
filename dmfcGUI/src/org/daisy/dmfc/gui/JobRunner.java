package org.daisy.dmfc.gui;




import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.Status;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class JobRunner implements Runnable {
	
	
	Shell shell;
	int jobNumber;
	int count;
	Job job;
	ScriptHandler scriptHandler;
	TableViewer tableViewer;
	TableViewer jobViewer;
	Button btnRun;
	
	public JobRunner(Shell _shell, Job job,  TableViewer _tableViewer,
			TableViewer _jobViewer){
		this.shell = _shell;
		this.scriptHandler=job.getScript();
		this.job=job;
		this.tableViewer=_tableViewer;
		this.jobViewer= _jobViewer;
		//this.btnRun = _btnRun;
		
	}
	
	
	public void run(){
		
		try{	
			//after the script handler is finished executing, set job to finished.
			scriptHandler.execute();			
			
//			finally, reset the status in the jobs table
//			after the script has finished..
			job.setStatus(Status.COMPLETED);
			
			
			UIManager.display.syncExec(new Runnable(){
				public void run(){
					jobViewer.refresh();
					//btnRun.setEnabled(true);
				}
			});
			
			
		}
		catch(ScriptException se){
			//if the script is not valid
			//set the script in the first table to status failed.
			final String exception=se.getMessage();
			job.setStatus(Status.FAILED);
			
			
			UIManager.display.syncExec(new Runnable(){
				public void run(){
					jobViewer.refresh();
					
					//show message to the user
					
					/*
					 MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					 SWT.CANCEL);
					 messageBox.setMessage(exception +"\n Please copy the above message \n " +
					 "the conversion details and \n give to your system administrator.");
					 messageBox.setText("Error:  Script Exception");
					 messageBox.open();	
					 */
				}
			});
			
		}
		catch(Exception e){
			//any other possible exceptions? This is not too informative.
			final String except=e.getMessage();
			job.setStatus(Status.FAILED);
			e.printStackTrace();
			UIManager.display.syncExec(new Runnable(){
				public void run(){
					jobViewer.refresh();
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.CANCEL);
					messageBox.setMessage(except + "\n Please copy the above message \n " +
					" and give to your system administrator.");
					messageBox.setText("Error");
					messageBox.open();
				}
			});	
		}
	}
}

