package org.daisy.dmfc.gui;




import org.daisy.dmfc.gui.widgetproperties.ColorChoices;
import org.daisy.dmfc.gui.widgetproperties.FontChoices;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * The manager creates the only display object in the application.
 * The static display is used for every screen created.
 * 
 * Images must be loaded first in SWT.
 * They also must be specifically disposed to prevent 
 * memory leaks.  This is true of any object that is created 
 * with a display (Images, Fonts, Colors)
 * @author Laurie Sherve
 */
public class UIManager {
	
	public static final Display display = new Display();
	public static int windowNum = 0;
	public static Image back, forward, stop;
	
	/**
	 * Load images first, if there are any to load.
	 *
	 */
	public static void load() {
		/*
		back = new Image(display, 5,5);
		forward = new Image(display, 5,5);
		stop = new Image(display, 5,5);
		*/
	}
	
	public static void open() {
		Window.getInstance().open();
		//new ConvertSingleFile().open();
		
		
		
	}
	
	
	public static void dispose() {
		/*  dispose of all images, fonts, colors
		back.dispose();
		forward.dispose();
		stop.dispose();
		*/
		for (int i = 0; i < FontChoices.fontResources.length; i++){
			   (FontChoices.fontResources[i]).dispose();
			}
		for (int i = 0; i < ColorChoices.colorResources.length; i++){
			   (ColorChoices.colorResources[i]).dispose();
			}
		
	}
}



