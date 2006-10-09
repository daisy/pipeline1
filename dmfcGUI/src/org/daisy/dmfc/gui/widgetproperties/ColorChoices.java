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
