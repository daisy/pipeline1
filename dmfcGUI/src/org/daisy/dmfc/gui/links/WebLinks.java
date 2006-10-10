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

package org.daisy.dmfc.gui.links;


import org.daisy.dmfc.gui.UIManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Provides links to internal and external files.
 * Files open in a browser window.
 * @author Laurie
 *
 */

public class WebLinks {

		Shell shell;
		Display display;
		
		
		public WebLinks(String url) {
			display=UIManager.display;
			shell=new Shell (display);
			shell.setLayout(new FillLayout());
			try {
				Browser browser= new Browser(shell, SWT.NONE);
					
				/* Load an HTML document */
				browser.setUrl(url);
				shell.open();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch())
						display.sleep();
				}
			} catch (SWTError e) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
						SWT.OK);
						messageBox.setMessage("Cannot display browser widget.\n\n" +
								"The error message is:\n" +
								e.getMessage());
						messageBox.setText("Browser widget error");
						messageBox.open();
			} finally {
				shell.dispose();
			}
		}
		
		

		public void createContents(){
			 Browser browser = new Browser(shell, SWT.NONE);
				
			/* Load an HTML document */
			browser.setUrl("http://www.daisy.org");
		}
	}
	
	

