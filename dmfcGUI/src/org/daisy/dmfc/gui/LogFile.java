package org.daisy.dmfc.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LogFile {
	
	Display display;
	Shell shell;
	String logContents;
	GridLayout layout;
	GridData data;
	
	
	public LogFile(String contents){
		this.logContents=contents;
		display= UIManager.display;
		shell = new Shell(display,SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM );
		shell.setText("DMFC Log File");
		shell.setLocation(150, 150);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns=1;
		gridLayout.makeColumnsEqualWidth=true;
		shell.setLayout(gridLayout);
		
		
		
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint=300;
		data.widthHint=500;
		Text txtLog = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		txtLog.setLayoutData(data);
		txtLog.setText(logContents);	
		shell.pack();
		
	}
	
	
	
	public void open() {
		shell.open();
		while (!shell.isDisposed())
			if (!UIManager.display.readAndDispatch())
				UIManager.display.sleep();
	}
	
	public void dispose() {
		shell.dispose();
	}
	
}
