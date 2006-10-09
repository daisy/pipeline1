/**
 *DAISY Multi-Format Converter, or DAISY Pipeline Graphical User Interface.
Copyright (C) 2006 DAISY Consortium

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.daisy.dmfc.gui;

import java.util.Iterator;
import java.util.List;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.gui.transformerlist.TransformerList;
import org.daisy.dmfc.qmanager.Job;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Shell;

/**
 * Runs the job queue in a separate thread.
 * @author Linus Ericson
 */
public class QueueRunner extends Thread {

	private List jobList;
	private TableViewer tableJobViewer;
	private Window window;
	private Shell shell;

	Job job = null;

	public QueueRunner(List jobQueue, Shell sh) {
		jobList = jobQueue;

		window = Window.getInstance();
		tableJobViewer = window.tableJobViewer;
		shell = sh;
	}

	public void run() {
		window.executing = true;
		Iterator it = jobList.iterator();
		while(it.hasNext()){

			//get the Job from the Queue
			job = (Job)it.next();
			job.setStatus(2);
			UIManager.display.syncExec(new Runnable() {
				public void run() {
					tableJobViewer.refresh();
					window.lblConversionRunning.setText(job.getScript().getName());
					window.transformerList = new TransformerList(job);
					window.tableViewer.setInput(window.transformerList);
					window.tableViewer.refresh();
					// Uncheck all items
					for (int i = 0; i < window.tableViewer.getTable().getItemCount(); ++i) {
						window.tableViewer.getTable().getItem(i).setChecked(false);
					}
				}
			});			

			// Update the LocalEventListener with the new job
			window.getLocalEventListener().setJob(job);

			ScriptHandler scriptHandler = job.getScript();

			//update the transformer table

			//add the input and output files to the script
			//actually, this only returns if the parameters are present in the script...
			scriptHandler.setProperty("input", job.getInputFile().getPath());
			if (job.getOutputFile()!=null){
				scriptHandler.setProperty("outputPath", job.getOutputFile().getPath());
			}

			JobRunner jr = new JobRunner (this.shell, job,  window.tableViewer, tableJobViewer);
			BusyCursor.showWhile(UIManager.display, jr);

			UIManager.display.syncExec(new Runnable() {
				public void run() {
					tableJobViewer.refresh();
				}
			});	

		}
		window.executing = false;
		UIManager.display.syncExec(new Runnable() {
			public void run() {
				window.btnRemoveFinishedJobs.setEnabled(true);
				window.btnRun.setEnabled(true);
			}
		});			
	}

}
