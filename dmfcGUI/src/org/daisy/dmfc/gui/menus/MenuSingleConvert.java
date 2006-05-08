package org.daisy.dmfc.gui.menus;

import org.daisy.dmfc.gui.ConvertSingleFile;
import org.daisy.dmfc.gui.UIManager;
import org.daisy.dmfc.gui.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MenuSingleConvert {

	Window window;
	ConvertSingleFile csf;
	
	public MenuSingleConvert(final Shell shell){
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		
//Top level "File"
		MenuItem file = new MenuItem(menu, SWT.CASCADE);
		file.setText("File");
		
		Menu filemenu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(filemenu);
		MenuItem actionItem = new MenuItem(filemenu, SWT.PUSH);
		actionItem.setText("Exit\tCtrl+X");	
		
		actionItem.setAccelerator(SWT.MOD1 + 'X');
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
		selectInputFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				csf = window.getConvertSingleFile();
				csf.setFileSelected();
			}
			});
		
		
		MenuItem setOuputPath = new MenuItem(actionmenu, SWT.PUSH);
		setOuputPath.setText("Select Output Path\tCtrl+O");
		setOuputPath.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				csf = window.getConvertSingleFile();
				csf.setOutputPathSelected();
			}
			});
		
		
		MenuItem ok = new MenuItem(actionmenu, SWT.PUSH);
		ok.setText("OK - Save\tCtrl+O");
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				csf = window.getConvertSingleFile();
				csf.sendJobInfoToMain();
			}
			});
		
		MenuItem cancel = new MenuItem(actionmenu, SWT.PUSH);
		cancel.setText("Cancel \tCtrl+C");
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				csf = window.getConvertSingleFile();
				csf.dispose();
			}
			});
		
		
		
	//Top Level "Help"
		MenuItem help = new MenuItem(menu, SWT.CASCADE);
		help.setText("Help");
		Menu helpmenu = new Menu(shell, SWT.DROP_DOWN);
		help.setMenu(helpmenu);
		
		MenuItem dmfcGuiHelp = new MenuItem(helpmenu, SWT.PUSH);
		dmfcGuiHelp.setText("GUI Help\tCtrl+H");
		
		MenuItem about = new MenuItem(helpmenu, SWT.PUSH);
		about.setText("Go to the TPB web site\tCtrl+T");
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
	
	
	}
}
