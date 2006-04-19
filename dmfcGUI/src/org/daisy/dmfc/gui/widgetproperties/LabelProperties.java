package org.daisy.dmfc.gui.widgetproperties;

import org.daisy.dmfc.gui.UIManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class LabelProperties implements IProperties {

	public LabelProperties(){}	
	
	public void setProperties(Control label, String text){
		((Label)label).setSize(150, 30 );
		//((Label)label).setFont(FontChoices.fontLabel);
		((Label)label).setBackground(new Color(UIManager.display, 255,255,255));
		((Label)label).setText(text);
		
	}
	
	
}
