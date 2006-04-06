package org.daisy.dmfc.gui.widgetproperties;

import org.daisy.dmfc.gui.UIManager;
import org.eclipse.swt.graphics.Color;

public class ColorChoices {

	final static public Color red = new Color(UIManager.display, 255,0,0);
	final static public Color white = new Color(UIManager.display, 255,255,255);
	final static public Color darkBlue = new Color(UIManager.display, 0,0, 175);
	final static public Color yellow = new Color(UIManager.display, 255,255,0);
	final static public Color ltBlue = new Color(UIManager.display, 132,112,255);
	
	final static public Color [] colorResources = {red, white, darkBlue, yellow, ltBlue};

}
