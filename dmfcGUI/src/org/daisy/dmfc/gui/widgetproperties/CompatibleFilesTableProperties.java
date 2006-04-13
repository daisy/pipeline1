package org.daisy.dmfc.gui.widgetproperties;

import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;


//Checked table
public class CompatibleFilesTableProperties {

	Table table;
	
	public CompatibleFilesTableProperties(Table _table){
		
		this.table=_table;
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(10,10,270,160);
		table.setFont(FontChoices.fontLabel);
		
	}
	
	public void populateTable(Text textField, Shell shell){
		FileDialog fd = new FileDialog(shell);
		String [] arStr= fd.getFilterExtensions();
		
		
		
	}
	
}
