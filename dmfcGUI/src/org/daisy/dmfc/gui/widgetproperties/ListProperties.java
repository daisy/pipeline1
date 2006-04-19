package org.daisy.dmfc.gui.widgetproperties;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;

public class ListProperties implements IProperties{

	
	public void setProperties (Control list, String text){
		//((List)list).setFont(FontChoices.fontList);
		((List)list).setBounds(0,0,150, 40);
		/*this.list.add("hello");
		this.list.add("there");
		this.list.add("hello");
		this.list.add("hello");
		this.list.add("hello there this is really really really long, what happerns?");
		*/
		
	}
	
	
}
