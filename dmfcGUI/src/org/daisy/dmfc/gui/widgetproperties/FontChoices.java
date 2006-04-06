package org.daisy.dmfc.gui.widgetproperties;

import org.daisy.dmfc.gui.UIManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

public class FontChoices {

	//final static public Font italic = new Font(UIManager.display,"Arial", 24, SWT.BOLD |SWT.ITALIC);
	final static public Font fontLabel = new Font(UIManager.display,"Arial", 11, SWT.NONE | SWT.COLOR_GREEN);
	final static public Font fontButton = new Font(UIManager.display,"Arial", 11, SWT.NONE | SWT.COLOR_GREEN);
	final static public Font fontTitle = new Font(UIManager.display,"Times New Roman", 24, SWT.BOLD );
	final static public Font fontSubTitle = new Font(UIManager.display,"Arial", 16, SWT.BOLD |SWT.COLOR_GREEN);
	final static public Font fontText = new Font(UIManager.display,"Arial", 11, SWT.NONE | SWT.COLOR_GREEN);
	final static public Font fontList = new Font(UIManager.display,"Arial", 11, SWT.NONE | SWT.COLOR_GREEN);

	//each font is disposed to prevent a memory leak when shell is disposed
	final static public Font [] fontResources = {fontLabel, fontButton, fontTitle, fontSubTitle, fontText, fontList};
}
