package org.daisy.dmfc.gui.widgetproperties;

import java.util.List;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TransformerListTableProperties {

	Table table;
	ScriptHandler scriptHandler;
	List listTransformerInfo;
	
	public TransformerListTableProperties(Table _table, ScriptHandler script){
		
		this.table=_table;
		this.scriptHandler=script;
		System.out.println("The name of the ScriptHandler is " + scriptHandler.getName());
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setFont(FontChoices.fontLabel);
		table.setBackground(ColorChoices.white);
		table.setBounds(0,0,300, 100);
	
		/*
		TableColumn tcCheckSelected = new TableColumn (table, SWT.LEFT);
		tcCheckSelected.setText("x");
		tcCheckSelected.setWidth(20);
		tcCheckSelected.setResizable(true);
		*/
		
		TableColumn tcConversionName = new TableColumn (table, SWT.LEFT);
		tcConversionName.setText("Names of Transformers in Conversion");
		tcConversionName.setWidth(350);
		tcConversionName.setResizable(true);
		
	}
	
	public void populateTable(){
		table.clearAll();
		
	
		listTransformerInfo = this.scriptHandler.getTransformerInfoList();
		if (listTransformerInfo!=null){
			int count=0;
			int size = listTransformerInfo.size();
			for (int i = 0; i<size; i++){
				TransformerHandler transHandler = (TransformerHandler)listTransformerInfo.get(i);
				TableItem ti = new TableItem(table ,SWT.NONE,count);
				ti.setText(new String[] {
				transHandler.getName()});
			
		}
		}
			else{
				TableItem tableItem = new TableItem(table ,SWT.NONE);
				tableItem.setText(scriptHandler.getName());
			}
				
		
		
		
		
	}
	
}

