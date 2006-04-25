package org.daisy.dmfc.gui.widgetproperties;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


//Checked table
public class CompatibleFilesTableProperties {

	Table table;
	TableColumn tcFiles;
	
	public CompatibleFilesTableProperties(Table _table){
		
		this.table=_table;
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(0,0,270,160);
		//table.setFont(FontChoices.fontLabel);
		
		tcFiles = new TableColumn (table, SWT.LEFT);
		tcFiles.setText("File Name");
		tcFiles.setWidth(500);
		tcFiles.setResizable(true);
		
	}
	
	public void populateTable(File file){
		if (file.isDirectory()){
			File [] arFiles = file.listFiles();
			
			for (int i = 0; i<arFiles.length; i++){
				
				File subFiles = arFiles[i];
				
				//only show  files
				if (subFiles.isFile()){
					TableItem ti = new TableItem(table ,SWT.NONE,0);
					ti.setText(subFiles.getAbsolutePath());
				}
			}
		}
	}
	
}
