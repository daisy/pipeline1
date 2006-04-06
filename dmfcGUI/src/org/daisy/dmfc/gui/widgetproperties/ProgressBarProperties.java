package org.daisy.dmfc.gui.widgetproperties;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ProgressBar;

//super (shell, SWT.HORIZONTAL |SWT.SMOOTH);

public class ProgressBarProperties implements IProperties{

	public ProgressBarProperties(){
	}

	public void setProperties(Control pb, String text){
		
		((ProgressBar)pb).setMinimum(0);
		((ProgressBar)pb).setMaximum(100);
		((ProgressBar)pb).setBounds(10,10,350,20);
		
	}
	
	
}
