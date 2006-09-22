package org.daisy.dmfc.gui.widgetproperties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.Queue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Sets properties of the Jobs Queue Table
 * All properties should be added to this file
 * Not the gui screen file.
 * Note:  You cannot subclass widgets in swt.
 * table created with super (shell, SWT.BORDER |SWT.V_SCROLL |SWT.MULTI |SWT.FULL_SELECTION);
 * @author Laurie Sherve
 *
 */

public class JobQueueTableProperties {

		Table table;
		TableColumn tcStatus;
		TableColumn tcJobs;
		TableColumn tcInputFile;
		TableColumn tcOutputFile;
		
	
	public JobQueueTableProperties(Table _table){
		
		table=_table;
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10,10,270,160);
		//table.setFont(FontChoices.fontLabel);
	
		tcStatus = new TableColumn (table, SWT.LEFT);
		tcStatus.setText("Status");
		tcStatus.setWidth(100);
		tcStatus.setResizable(true);
	
		tcJobs = new TableColumn (table, SWT.LEFT);
		tcJobs.setText("Types of Conversion/s");
		tcJobs.setWidth(175);
		tcJobs.setResizable(true);
	
		tcInputFile = new TableColumn (table, SWT.LEFT);
		tcInputFile.setText("Source");
		tcInputFile.setWidth(200);
		tcInputFile.setResizable(true);
		
		tcOutputFile = new TableColumn (table, SWT.LEFT);
		tcOutputFile.setText("Destination");
		tcOutputFile.setWidth(200);
		tcOutputFile.setResizable(true);
	
	}
	
	
	
		public String getStatus(int status){
			String strStatus = "";
			switch (status){
				case 1:
					strStatus= "Waiting";
					break;
				case 2:
					strStatus = "In Progress";
					break;
				case 3:
					strStatus = "Completed";
					break;
				case 4:
					strStatus= "Failed";
					break;
				default:
					strStatus = "Bad Result";
					//throw ThisShouldNeverHappenException
					break;
			}
			return strStatus;
				
		}
	
}