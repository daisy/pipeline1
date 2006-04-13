package org.daisy.dmfc.gui.menus;

import org.daisy.dmfc.gui.UIManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MenuDMFC {
	
	public MenuDMFC(final Shell shell){
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		
//Top level "File"
		MenuItem file = new MenuItem(menu, SWT.CASCADE);
		file.setText("File");
		
		Menu filemenu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(filemenu);
		MenuItem actionItem = new MenuItem(filemenu, SWT.PUSH);
		actionItem.setText("Exit\tCtrl+Q");	
		
		actionItem.setAccelerator(SWT.MOD1 + 'Q');
		actionItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				UIManager.dispose();
				System.exit(0);
			}
		});
	/*
		shell.addShellListener(new ShellAdapter() {
	        public void shellClosed(ShellEvent e) {
	            MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
	            mb.setText("Confirm Exit");
	            mb.setMessage("Are you sure you want to exit?");
	            int rc = mb.open();
	            e.doit = rc == SWT.OK;
	        }
	});
		
	*/	
		
//	Top Level "View"
		MenuItem view = new MenuItem(menu, SWT.CASCADE);
		view.setText("View");
		Menu viewmenu = new Menu(shell, SWT.DROP_DOWN);
		view.setMenu(viewmenu);
		
		MenuItem enableJobDetails = new MenuItem(viewmenu, SWT.PUSH);
		enableJobDetails.setText("Enable Job Details\tCtrl+E");
		
		enableJobDetails.setAccelerator(SWT.MOD1 + 'E');
		enableJobDetails.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			System.out.println("Enabling");
			}
			});
		
		
		MenuItem disableJobDetails = new MenuItem(viewmenu, SWT.PUSH);
		disableJobDetails.setText("Disable Job Details\tCtrl+D");
		
		disableJobDetails.setAccelerator(SWT.MOD1 + 'D');
		disableJobDetails.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
			System.out.println("Dis-enabling");
			}
			});
		
	//Top Level "Action"
		
		MenuItem action = new MenuItem(menu, SWT.CASCADE);
		action.setText("Action");
		Menu actionmenu = new Menu(shell, SWT.DROP_DOWN);
		action.setMenu(actionmenu);
	
		MenuItem selectJobDetails = new MenuItem(actionmenu, SWT.PUSH);
		selectJobDetails.setText("Select Conversion\tCtrl+S");
		
		MenuItem manageJobsInQueue = new MenuItem(actionmenu, SWT.PUSH);
		manageJobsInQueue.setText("Manage Jobs in Queue\tCtrl+J");
		
		MenuItem runConversions = new MenuItem(actionmenu, SWT.PUSH);
		runConversions.setText("Run Conversions\tCtrl+R");
		
		MenuItem viewJobDetails = new MenuItem(actionmenu, SWT.PUSH);
		viewJobDetails.setText("View Job Details\tCtrl+T");
		
		
		
	//Top Level "Help"
		MenuItem help = new MenuItem(menu, SWT.CASCADE);
		help.setText("Help");
		Menu helpmenu = new Menu(shell, SWT.DROP_DOWN);
		help.setMenu(helpmenu);
		
		MenuItem dmfcGuiHelp = new MenuItem(helpmenu, SWT.PUSH);
		dmfcGuiHelp.setText("GUI Help\tCtrl+H");
		
		MenuItem contactUs = new MenuItem(helpmenu, SWT.PUSH);
		contactUs.setText("Contact Us\tCtrl+U");
		
		MenuItem daisyWebSite = new MenuItem(helpmenu, SWT.PUSH);
		daisyWebSite.setText("Go to the DAISY web site\tCtrl+D");
		
		MenuItem tpbWebSite = new MenuItem(helpmenu, SWT.PUSH);
		tpbWebSite.setText("Go to the TPB web site\tCtrl+B");
	
	
	}

}

