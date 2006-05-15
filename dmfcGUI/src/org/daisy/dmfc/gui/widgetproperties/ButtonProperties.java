package org.daisy.dmfc.gui.widgetproperties;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ButtonProperties implements IProperties{

	public ButtonProperties(){}
		
	public void setProperties(Control btn, String txt){
		
		//((Button)btn).setFont(FontChoices.fontButton);
		//((Button)btn).setBackground(ColorChoices.white);
		((Button)btn).setText(txt);	
	}
}
	
	

