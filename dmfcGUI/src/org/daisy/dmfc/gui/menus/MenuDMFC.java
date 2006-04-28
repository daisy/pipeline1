package org.daisy.dmfc.gui.menus;

import org.daisy.dmfc.gui.ConvertMultipleFiles;
import org.daisy.dmfc.gui.CurrentJobDetails;
import org.daisy.dmfc.gui.UIManager;
import org.daisy.dmfc.gui.Window;
import org.daisy.dmfc.gui.links.WebLinks;
import org.daisy.dmfc.qmanager.Queue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class MenuDMFC {
	
	Window window;
	Queue cue;
	
	//MenuItems, accessed from main Window.
	MenuItem enableJobDetails;
	MenuItem selectJobDetails;
	MenuItem convertMultipleFile;
	MenuItem runConversions;
	
	
	public MenuDMFC(final Shell shell){
		
		cue=Queue.getInstance();		
		Menu menu = new Menu(shell, SWT.BAR);
		//sets menu as main menu for this shell
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

		
//	Top Level "View"
		MenuItem view = new MenuItem(menu, SWT.CASCADE);
		view.setText("View");
		Menu viewmenu = new Menu(shell, SWT.DROP_DOWN);
		view.setMenu(viewmenu);
		
		enableJobDetails = new MenuItem(viewmenu, SWT.PUSH);
		enableJobDetails.setText("Conversion Details\tCtrl+E");
		enableJobDetails.setEnabled(true);
		enableJobDetails.setAccelerator(SWT.MOD1 + 'E');
		enableJobDetails.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				if (window.executing==true){
					CurrentJobDetails.getInstance().open();
				}	
				else{
					System.out.println("Should not be enabled.");
				}
			}
		});
		
		
	
		
	//Top Level "Action"
		
		MenuItem action = new MenuItem(menu, SWT.CASCADE);
		action.setText("Action");
		Menu actionmenu = new Menu(shell, SWT.DROP_DOWN);
		action.setMenu(actionmenu);
	
		
		selectJobDetails = new MenuItem(actionmenu, SWT.PUSH);
		selectJobDetails.setText("Select Conversion Options\tCtrl+S");
		selectJobDetails.setAccelerator(SWT.MOD1 + 'S');
		//focus listener on the conversions
		selectJobDetails.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.listConversion.forceFocus();
			}
			});
		
		
		
		convertMultipleFile = new MenuItem(actionmenu, SWT.PUSH);
		convertMultipleFile.setText("Select Files to be Converted\tCtrl+F");
		convertMultipleFile.setAccelerator(SWT.MOD1 + 'F');
		
		convertMultipleFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				if (window.listConversion.getSelectionCount()==1){
					
					
					window.getConversionSelection();
					window.getNewCMFScreen();
					
					
					//window.getConversionSelection();
					//new ConvertMultipleFiles().open();
				}
				else{
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
							SWT.CANCEL);
							messageBox.setMessage("There are no conversions chosen \n" +
									"Please choose a conversion first.");
							messageBox.setText("Error: No conversions chosen");
							messageBox.open();
				}
			}
			});
		
		
	//Manage Job List - has submenus
		
		MenuItem manageJobsInQueue = new MenuItem(actionmenu, SWT.CASCADE);
		manageJobsInQueue.setText("Manage Jobs in List");
		
		
		
		
		//Submenu of the Manage Job List menu item
		Menu submenu = new Menu(shell, SWT.DROP_DOWN);
		manageJobsInQueue.setMenu(submenu);
		final MenuItem subactionItemUp = new MenuItem(submenu, SWT.PUSH);
		subactionItemUp.setText("&Move Up List\tCtrl+U");
		subactionItemUp.setAccelerator(SWT.CTRL+'U');
		subactionItemUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.tblJobs2.forceFocus();
				window.moveJobUp();
			}
			});
		
		final MenuItem subactionItemDown = new MenuItem(submenu, SWT.PUSH);
		subactionItemDown.setText("&Move Down List\tCtrl+D");
		subactionItemDown.setAccelerator(SWT.CTRL+'D');
		subactionItemDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.tblJobs2.forceFocus();
				window.moveJobDown();
			}
			});
		
		
		final MenuItem subactionItemRemove = new MenuItem(submenu, SWT.PUSH);
		subactionItemRemove.setText("&Remove From List\tCtrl+R");
		subactionItemRemove.setAccelerator(SWT.CTRL+'R');
		subactionItemRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.tblJobs2.forceFocus();
				window.deleteJob();
			}
			});
		
		
		final MenuItem subactionItemChange = new MenuItem(submenu, SWT.PUSH);
		subactionItemChange.setText("&Change File Selection\tCtrl+N");
		subactionItemChange.setAccelerator(SWT.CTRL+'N');
		subactionItemChange.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.tblJobs2.forceFocus();
				window.editJob();
			}
			});
		
		
	//Run, View Details, Terminate Conversions
		
		runConversions = new MenuItem(actionmenu, SWT.PUSH);
		runConversions.setText("Run All Jobs\tCtrl+R");
		runConversions.setAccelerator(SWT.MOD1 + 'R');
		runConversions.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
		
				if (cue.getLinkedListJobs().isEmpty()){
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
							SWT.CANCEL);
							messageBox.setMessage("There are no conversions chosen \n" +
									"Please choose a conversion first.");
							messageBox.setText("Error: No conversions chosen");
							messageBox.open();
				}
				else{
					window= Window.getInstance();
					window.runScript();
				}
			}
		});
		
		/*
		MenuItem viewJobDetails = new MenuItem(actionmenu, SWT.PUSH);
		viewJobDetails.setText("View Job Details\tCtrl+D");
		viewJobDetails.setAccelerator(SWT.MOD1 + 'D');
		*/
		MenuItem terminateConversion = new MenuItem(actionmenu, SWT.PUSH);
		terminateConversion.setText("Terminate Conversions\tCtrl+T");
		terminateConversion.setAccelerator(SWT.MOD1 + 'T');
		terminateConversion.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				
			}
		});
				
		
		
		
	//Top Level "Help"
		MenuItem help = new MenuItem(menu, SWT.CASCADE);
		help.setText("Help");
		Menu helpmenu = new Menu(shell, SWT.DROP_DOWN);
		help.setMenu(helpmenu);
		help.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				
			}
			});
		
		MenuItem dmfcGuiHelp = new MenuItem(helpmenu, SWT.PUSH);
		dmfcGuiHelp.setText("Help\tCtrl+H");
		dmfcGuiHelp.setAccelerator(SWT.MOD1 + 'H');
		dmfcGuiHelp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new WebLinks("http://www.daisy.org/projects/dmfc/HelpGui/index.html");
				
			}
			});
		
		
		MenuItem daisyWebSite = new MenuItem(helpmenu, SWT.PUSH);
		daisyWebSite.setText("Go to the DAISY web site\tCtrl+D");
		daisyWebSite.setAccelerator(SWT.MOD1 + 'D');
		daisyWebSite.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new WebLinks("http://www.daisy.org");
				
			}
			});
		
		MenuItem about = new MenuItem(helpmenu, SWT.PUSH);
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
	
	
	}


	public MenuItem getConvertMultipleFile() {
		return convertMultipleFile;
	}


	public void setConvertMultipleFile(MenuItem convertMultipleFile) {
		this.convertMultipleFile = convertMultipleFile;
	}


	public MenuItem getEnableJobDetails() {
		return enableJobDetails;
	}


	public void setEnableJobDetails(MenuItem enableJobDetails) {
		this.enableJobDetails = enableJobDetails;
	}


	public MenuItem getRunConversions() {
		return runConversions;
	}


	public void setRunConversions(MenuItem runConversions) {
		this.runConversions = runConversions;
	}


	public MenuItem getSelectJobDetails() {
		return selectJobDetails;
	}


	public void setSelectJobDetails(MenuItem selectJobDetails) {
		this.selectJobDetails = selectJobDetails;
	}
	
	
	

}

