package org.daisy.dmfc.gui.widgetproperties;

import org.eclipse.swt.widgets.Table;


//Checked table
public class CompatibleFilesTableProperties {

	Table table;
	
	public CompatibleFilesTableProperties(Table _table){
		
		this.table=_table;
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		//table.setBounds(10,10,270,160);
		table.setFont(FontChoices.fontLabel);
		
		
	}
	
	
}
