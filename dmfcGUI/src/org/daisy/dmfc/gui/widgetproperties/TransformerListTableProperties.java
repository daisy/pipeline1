
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

package org.daisy.dmfc.gui.widgetproperties;

import java.util.List;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Used with a checkbox table 
 * @author Laurie
 *
 */

public class TransformerListTableProperties {

	Table table;
	ScriptHandler scriptHandler;
	List listTransformerInfo;
	
	public TransformerListTableProperties(Table _table){
		this.table=_table;
		createTable();
	}
	
	public TransformerListTableProperties(Table _table, ScriptHandler script){
		
		this.table=_table;
		this.scriptHandler=script;
		
		
		createTable();
	}
	
	public void createTable(){
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBackground(ColorChoices.white);
		//table.setBounds(0,0,300, 200);
	
		TableColumn tcConversionName = new TableColumn (table, SWT.LEFT);
		tcConversionName.setText("Transformers in Conversion");
		tcConversionName.setWidth(175);
		tcConversionName.setResizable(true);
		
	}
	
	
	/**
	 * not used - jface took over
	 *
	 */
	public void populateTable(){
		table.clearAll();
	
		listTransformerInfo = this.scriptHandler.getTransformerInfoList();
		if (listTransformerInfo!=null){
			int count=0;
			int size = listTransformerInfo.size();
			System.out.println("Transformer List, how many? " + size);
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

