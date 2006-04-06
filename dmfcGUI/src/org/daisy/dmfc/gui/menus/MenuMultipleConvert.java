package org.daisy.dmfc.gui.menus;

import org.daisy.dmfc.gui.UIManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MenuMultipleConvert {

	public MenuMultipleConvert(final Shell shell){
		
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
		
		
//Top Level "Action"
		
		MenuItem action = new MenuItem(menu, SWT.CASCADE);
		action.setText("Action");
		Menu actionmenu = new Menu(shell, SWT.DROP_DOWN);
		action.setMenu(actionmenu);
	
		MenuItem selectFolder = new MenuItem(actionmenu, SWT.PUSH);
		selectFolder.setText("Select Folder\tCtrl+F");
		
		MenuItem selectFiles = new MenuItem(actionmenu, SWT.PUSH);
		selectFiles.setText("Select Compatible Files\tCtrl+L");
		
		MenuItem setOuputPath = new MenuItem(actionmenu, SWT.PUSH);
		setOuputPath.setText("Select Output Path\tCtrl+O");
		
		MenuItem ok = new MenuItem(actionmenu, SWT.PUSH);
		ok.setText("OK - Save\tCtrl+O");
		
		MenuItem cancel = new MenuItem(actionmenu, SWT.PUSH);
		cancel.setText("Cancel \tCtrl+C");
		
		
		
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
