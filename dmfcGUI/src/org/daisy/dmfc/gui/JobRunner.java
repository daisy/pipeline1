package org.daisy.dmfc.gui;




import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.LocalEventListener;
import org.daisy.dmfc.qmanager.Status;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class JobRunner extends Thread{
	
	
	Shell shell;
	ProgressBar pb;
	Text elapsed;
	Text estimated;
	Text conversion;
	int jobNumber;
	int count;
	Job job;
	ScriptHandler scriptHandler;
	LocalEventListener lel;
	TableViewer tableViewer;
	TableViewer jobViewer;
	
	
	public JobRunner(Shell _shell, ScriptHandler _scriptHandler, Job job, ProgressBar pb, 
			Text txtElapsedTime, Text txtEstimatedTime, LocalEventListener _lel, TableViewer _tableViewer,
			TableViewer _jobViewer){
		this.shell = _shell;
		this.scriptHandler=_scriptHandler;
		this.job=job;
		this.pb=pb;
		this.elapsed=txtElapsedTime;
		this.estimated=txtEstimatedTime;
		this.lel=_lel;
		this.tableViewer=_tableViewer;
		this.jobViewer= _jobViewer;
		
	}
	
	
	public void run(){
		
		try{	
			//after the script handler is finished executing, set job to finished.
			scriptHandler.execute();
			int transNumber = job.getScript().getCurrentTaskIndex();
			//System.out.println("what is the current task index? " + transNumber);
			count++;
			
			UIManager.display.syncExec(new Runnable(){
				public void run(){
					int count = -1;
					
					pb.setSelection((int)(lel.getProgress() * 100));
					elapsed.setText(String.valueOf(lel.getTimeLeft()));
					estimated.setText(String.valueOf(lel.getTotalTime()));
					
					System.out.println("Progress Bar progress is " + lel.getProgress()*100);
					System.out.println("Elapsed progress is " + lel.getTimeLeft());
					System.out.println("Estimated progress is " + lel.getTotalTime());
					
					count++;	
				}
			});
			
			
			UIManager.display.syncExec(new Runnable(){
				public void run(){
					tableViewer.getTable().getItem(job.getScript().getCurrentTaskIndex()).setChecked(true);
				}
			});
			
			
			
//			finally, reset the status in the jobs table
//			after the script has finished..
			job.setStatus(Status.COMPLETED);
			
			UIManager.display.syncExec(new Runnable(){
				public void run(){
					jobViewer.refresh();	
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
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
							SWT.CANCEL);
					messageBox.setMessage(exception +"\n Please copy the above message \n " +
					"the conversion details and \n give to your system administrator.");
					messageBox.setText("Error:  Script Exception");
					messageBox.open();	
				}
			});
			
		}
		catch(Exception e){
			//any other possible exceptions? This is not too informative.
			final String except=e.getMessage();
			job.setStatus(Status.FAILED);
			
			UIManager.display.syncExec(new Runnable(){
				public void run(){
					jobViewer.refresh();
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
							SWT.CANCEL);
					messageBox.setMessage(except + "\n Please copy the above message \n " +
					" and give to your system administrator.");
					messageBox.setText("Error");
					messageBox.open();
				}
			});	
		}
	}
}

