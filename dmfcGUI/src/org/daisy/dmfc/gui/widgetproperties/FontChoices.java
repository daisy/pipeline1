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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

public class FontChoices {

	//final static public Font italic = new Font(UIManager.display,"Arial", 24, SWT.BOLD |SWT.ITALIC);
	final static public Font fontLabel = new Font(UIManager.display,"Arial", 10, SWT.NONE | SWT.COLOR_BLACK);
	final static public Font fontButton = new Font(UIManager.display,"Arial", 11, SWT.NONE | SWT.COLOR_GREEN);
	final static public Font fontTitle = new Font(UIManager.display,"Arial", 18, SWT.BOLD );
	final static public Font fontSubTitle = new Font(UIManager.display,"Arial", 16, SWT.BOLD |SWT.COLOR_GREEN);
	final static public Font fontText = new Font(UIManager.display,"Arial", 11, SWT.NONE | SWT.COLOR_GREEN);
	final static public Font fontList = new Font(UIManager.display,"Arial", 11, SWT.NONE | SWT.COLOR_GREEN);

	//each font is disposed to prevent a memory leak when shell is disposed
	final static public Font [] fontResources = {fontLabel, fontButton, fontTitle, fontSubTitle, fontText, fontList};
}
