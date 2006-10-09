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
		
		if (contents!=null ){
			this.logContents=contents;
		}
		else
			this.logContents="";
		
		display= UIManager.display;
		
		shell = new Shell(display );
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
