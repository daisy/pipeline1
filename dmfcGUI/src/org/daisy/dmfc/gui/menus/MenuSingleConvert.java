package org.daisy.dmfc.gui.menus;

import org.daisy.dmfc.gui.UIManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MenuSingleConvert {

	public MenuSingleConvert(final Shell shell){
		
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
				MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
	            mb.setText("Confirm Exit");
	            mb.setMessage("Are you sure you want to exit?");
				int close = mb.open();
				switch (close){
					case SWT.OK:
						UIManager.dispose();
						System.exit(0);
						break;
					case SWT.CANCEL:
						break;
				}
			}
		});
		
//Top Level "Action"
		
		MenuItem action = new MenuItem(menu, SWT.CASCADE);
		action.setText("Action");
		Menu actionmenu = new Menu(shell, SWT.DROP_DOWN);
		action.setMenu(actionmenu);
	
		MenuItem selectInputFile = new MenuItem(actionmenu, SWT.PUSH);
		selectInputFile.setText("Select Input File\tCtrl+F");
		
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
