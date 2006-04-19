package org.daisy.dmfc.gui.widgetproperties;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class TextProperties implements IProperties {

	
	public TextProperties(){
	}
	
	public void setProperties(Control text, String str){
		//initially, all text fields will be null.
		
		//this is not editable, but it is changeable
		((Text)text).setEditable(false);
		//((Text)text).setFont(FontChoices.fontLabel);
		((Text)text).setBackground(ColorChoices.yellow);
	}
	
		
}
