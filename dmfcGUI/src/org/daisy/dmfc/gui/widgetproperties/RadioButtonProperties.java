package org.daisy.dmfc.gui.widgetproperties;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

public class RadioButtonProperties implements IProperties{

	public RadioButtonProperties(){
	}	
	public void setProperties(Control radioButton, String message){
		
		((Button)radioButton).setText(message);
		((Button)radioButton).setFont(FontChoices.fontButton);
		((Button)radioButton).setBackground(ColorChoices.white);	
	}
	
}
