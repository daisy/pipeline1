package org.daisy.dmfc.gui.links;


import org.daisy.dmfc.gui.UIManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class WebLinks {

		Shell shell;
		Display display;
		
		
		public WebLinks(String url) {
			display=UIManager.display;
			shell=new Shell (display);
			shell.setLayout(new FillLayout());
			Browser browser= new Browser(shell, SWT.NONE);
				
			/* Load an HTML document */
			browser.setUrl(url);
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			shell.dispose();
		}
		
		

		public void createContents(){
			 Browser browser = new Browser(shell, SWT.NONE);
				
			/* Load an HTML document */
			browser.setUrl("http://www.daisy.org");
		}
	}
	
	

