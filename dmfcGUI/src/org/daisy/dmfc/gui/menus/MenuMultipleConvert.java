package org.daisy.dmfc.gui.menus;

import org.daisy.dmfc.gui.ConvertMultipleFiles;
import org.daisy.dmfc.gui.UIManager;
import org.daisy.dmfc.gui.Window;
import org.daisy.dmfc.gui.links.WebLinks;
import org.daisy.dmfc.qmanager.Queue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MenuMultipleConvert {
	
	ConvertMultipleFiles cmf;
	Window window;
	Queue cue;

	public MenuMultipleConvert(final Shell shell){
		
		Menu menu = new Menu(shell, SWT.BAR);
		//for this shell, this is the main menu
		shell.setMenuBar(menu);
		
		
		
//Top level "File"
		MenuItem file = new MenuItem(menu, SWT.CASCADE);
		file.setText("File");
		
		Menu filemenu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(filemenu);
		MenuItem actionItem = new MenuItem(filemenu, SWT.PUSH);
		
		//actionItem.setText("Exit\tCtrl+X");	
		//actionItem.setAccelerator(SWT.MOD1 + 'X');
		
		actionItem.setText("Exit");	
		
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
	
		MenuItem selectFolder = new MenuItem(actionmenu, SWT.PUSH);
		selectFolder.setText("Search for Compatible Sources");
		selectFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.getConvertMultipleFiles().setDirectorySelected();
				window.getConvertMultipleFiles().populateCompatibleFilesTable();	
			}
			});
		
		
		MenuItem selectFiles = new MenuItem(actionmenu, SWT.PUSH);
		selectFiles.setText("Select Compatible Sources");
		selectFiles.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.getConvertMultipleFiles().getTableCompatibleFiles().forceFocus();
			}
			});
		
		
		MenuItem setOuputPath = new MenuItem(actionmenu, SWT.PUSH);
		setOuputPath.setText("Select Output Path");
		setOuputPath.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				//force focus on radio buttons
				window.getConvertMultipleFiles().btnRadio1.forceFocus();
			}
			});
		
		
		
		MenuItem ok = new MenuItem(actionmenu, SWT.PUSH);
		ok.setText("OK - Save\tCtrl+O");
		ok.setAccelerator(SWT.MOD1 + 'O');
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=window.getInstance();
				window.getConvertMultipleFiles().sendJobInfoToMain();
			}
			});
		
		
		
		MenuItem cancel = new MenuItem(actionmenu, SWT.PUSH);
		cancel.setText("Cancel \tCtrl+N");
		cancel.setAccelerator(SWT.MOD1 + 'N');
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.getConvertMultipleFiles().dispose();
			}
			});
		
		
		
	//Top Level "Help"
		MenuItem help = new MenuItem(menu, SWT.CASCADE);
		help.setText("Help");
		Menu helpmenu = new Menu(shell, SWT.DROP_DOWN);
		help.setMenu(helpmenu);
		
		
		MenuItem dmfcGuiHelp = new MenuItem(helpmenu, SWT.PUSH);
		dmfcGuiHelp.setText("GUI Help\tCtrl+H");
		dmfcGuiHelp.setAccelerator(SWT.MOD1 + 'H');
		dmfcGuiHelp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new WebLinks("http://www.daisy.org/projects/dmfc/HelpGui/index.html");
			}
			});
		
		
		MenuItem daisyWebSite = new MenuItem(helpmenu, SWT.PUSH);
		
		//daisyWebSite.setText("Go to the DAISY web site\tCtrl+D");
		//daisyWebSite.setAccelerator(SWT.MOD1 + 'D');
		
		daisyWebSite.setText("Go to the DAISY web site");
		
		daisyWebSite.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new WebLinks("http://www.daisy.org");
			}
			});
		
	/*	MenuItem about = new MenuItem(helpmenu, SWT.PUSH);
		about.setText("About\tCtrl+A");
		about.setAccelerator(SWT.MOD1 + 'A');
		about.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("AboutScreen");
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION |
						SWT.CANCEL);
						messageBox.setMessage("DAISY Multi-Format Converter \n\n" +
								"Copyright by the DAISY Consortium 2006 \n" +
								"Version 1.0, June 2006"
								);
						messageBox.setText("About");
						messageBox.open();
			}
			});
	*/
	
	}
	
}
