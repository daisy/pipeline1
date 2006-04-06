package org.daisy.dmfc.gui.widgetproperties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TransformerListTableProperties {

	Table table;
	
	public TransformerListTableProperties(Table _table){
		
		this.table=_table;
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setFont(FontChoices.fontLabel);
		table.setBackground(ColorChoices.white);
		table.setBounds(0,0,300, 100);
	
		
		TableColumn tcCheckSelected = new TableColumn (table, SWT.LEFT);
		tcCheckSelected.setText("x");
		tcCheckSelected.setWidth(20);
		tcCheckSelected.setResizable(true);
		
		TableColumn tcConversionName = new TableColumn (table, SWT.LEFT);
		tcConversionName.setText("Names of Transformers in Conversion");
		tcConversionName.setWidth(350);
		tcConversionName.setResizable(true);
		
	}
}
