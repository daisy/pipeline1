
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



