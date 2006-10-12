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

package org.daisy.dmfc.gui.menus;

import java.io.File;

import org.daisy.dmfc.gui.ConvertMultipleFiles;
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

/**
 * Main menu for the application
 * @author Laurie Sherve
 *
 */


public class MenuDMFC {
	
	Window window;
	Queue cue;

	
	//MenuItems
	MenuItem viewLogFile;
	MenuItem selectJobDetails;
	MenuItem convertMultipleFile;
	MenuItem convertSingleFile;
	MenuItem runConversions;
	MenuItem viewHideRunDetails;
	
	
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
		
		//actionItem.setText("Exit\tCtrl+X");	
		// actionItem.setAccelerator(SWT.MOD1 + 'X');
		
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
	
		
		selectJobDetails = new MenuItem(actionmenu, SWT.PUSH);
		
		//selectJobDetails.setText("Select A Conversion \tCtrl+C");
		//selectJobDetails.setAccelerator(SWT.MOD1 + 'C');
		
		selectJobDetails.setText("Select Converter");
		
		//focus listener on the conversions
		selectJobDetails.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.getTreeFromTreeViewer().forceFocus();
			}
			});
		
	/*	@todo - for release 3 - add back in
	 * 
		convertMultipleFile = new MenuItem(actionmenu, SWT.PUSH);
		
		convertMultipleFile.setText("Add Multiple Sources\tCtrl+M");
		convertMultipleFile.setAccelerator(SWT.MOD1 + 'M');
		
		convertMultipleFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
							
				window=Window.getInstance();
				window.getConversionSelection();
				if (window.scriptHandler!=null){
					window.getNewCMFScreen();
						
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
		
		*/
		
		convertSingleFile = new MenuItem(actionmenu, SWT.PUSH);
		convertSingleFile.setText("Add Single Source\tCtrl+S");
		convertSingleFile.setAccelerator(SWT.MOD1 + 'S');
		
		convertSingleFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				window=Window.getInstance();
				window.getConversionSelection();
				
				if (window.scriptHandler!=null){
					window.getNewSingleFileScreen();
						
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
		
		final MenuItem subactionSelectJob = new MenuItem(submenu, SWT.PUSH);
		subactionSelectJob.setText("Select Job From List");
		
	//	subactionSelectJob.setText("&Select Job From List\tCtrl+J");
	//	subactionSelectJob.setAccelerator(SWT.CTRL+'J');
		
		subactionSelectJob.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.tblJobs2.forceFocus();
				window.setSelectedIndex(window.tblJobs2.getSelectionIndex());
			}
			});
		
		final MenuItem subactionItemUp = new MenuItem(submenu, SWT.PUSH);
		subactionItemUp.setText("Move Job Up");
		//subactionItemUp.setText("&Move Up List\tCtrl+U");
		//subactionItemUp.setAccelerator(SWT.CTRL+'U');
		subactionItemUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.setSelectedIndex(window.tblJobs2.getSelectionIndex());
				window.moveJobUp();
				
			}
			});
		
		final MenuItem subactionItemDown = new MenuItem(submenu, SWT.PUSH);
		subactionItemDown.setText("Move Job Down");
		//subactionItemDown.setText("&Move Down List\tCtrl+W");
		//subactionItemDown.setAccelerator(SWT.CTRL+'W');
		subactionItemDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.setSelectedIndex(window.tblJobs2.getSelectionIndex());
				window.moveJobDown();
			}
			});
		
		
		final MenuItem subactionItemRemove = new MenuItem(submenu, SWT.PUSH);
		subactionItemRemove.setText("Remove Job");
		//subactionItemRemove.setText("&Remove From List\tCtrl+M");
		//subactionItemRemove.setAccelerator(SWT.CTRL+'M');
		subactionItemRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.setSelectedIndex(window.tblJobs2.getSelectionIndex());
				window.deleteJobs();
			}
			});
		
		
		final MenuItem subactionItemChange = new MenuItem(submenu, SWT.PUSH);
		subactionItemChange.setText("Change Selected Sources");
		//subactionItemChange.setText("&Change File Selection\tCtrl+N");
		//subactionItemChange.setAccelerator(SWT.CTRL+'N');
		subactionItemChange.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.setSelectedIndex(window.tblJobs2.getSelectionIndex());
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
					window.start();
					
				}
			}
		});
		
		
		MenuItem terminateConversion = new MenuItem(actionmenu, SWT.PUSH);
		//terminateConversion.setText("Terminate Jobs\tCtrl+T");
		//terminateConversion.setAccelerator(SWT.MOD1 + 'T');
		terminateConversion.setText("Cancel All Jobs");
		
		terminateConversion.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.terminateJob();
				
			}
		});
				
		/*
		MenuItem startOver = new MenuItem(actionmenu, SWT.PUSH);
		startOver.setText("Start Over\tCtrl+O");
		startOver.setAccelerator(SWT.MOD1 + 'O');
		startOver.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.restartConverter();
				
			}
		});
	*/
		
		
//		Top Level "View"
		MenuItem view = new MenuItem(menu, SWT.CASCADE);
		view.setText("View");
		Menu viewmenu = new Menu(shell, SWT.DROP_DOWN);
		view.setMenu(viewmenu);
		
		viewHideRunDetails = new MenuItem(viewmenu, SWT.PUSH);
		
		//viewHideRunDetails.setText("Hide Run Details \tCtrl+V");
		//viewHideRunDetails.setAccelerator(SWT.MOD1 + 'V');
		
		viewHideRunDetails.setText("Hide Run Details");
		
		//show or hide the conversion/run details
		viewHideRunDetails.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				window.viewRunDetails();
				viewHideRunDetails.setText(window.getStrViewRunDetails());
			}
			});

		
		
		
	//	Top Level "LogFile"
	
		MenuItem logFile = new MenuItem(menu, SWT.CASCADE);
		logFile.setText("Log File");
		Menu logmenu = new Menu(shell, SWT.DROP_DOWN);
		logFile.setMenu(logmenu);
		
		viewLogFile = new MenuItem(logmenu, SWT.PUSH);
		
		viewLogFile.setText("View Log File");
		//viewLogFile.setText("View Log File\tCtrl+L");
		//viewLogFile.setAccelerator(SWT.MOD1 + 'L');
		
		viewLogFile.setEnabled(true);
		
		viewLogFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				window=Window.getInstance();
				
					window = Window.getInstance();
					window.getLogFile();
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
				String curDir = System.getProperty("user.dir");
				System.out.println("the current user dir is: "+ curDir);
				String strHelp = new String(curDir+ File.separator + "helpFiles" + File.separator + "index.html");
				
				new WebLinks(strHelp);
				
			}
			});
		
		
		MenuItem daisyWebSite = new MenuItem(helpmenu, SWT.PUSH);
		daisyWebSite.setText("Go to the DAISY web site");
		//daisyWebSite.setText("Go to the DAISY web site\tCtrl+D");
		//daisyWebSite.setAccelerator(SWT.MOD1 + 'D');
		
		daisyWebSite.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new WebLinks("http://www.daisy.org");
				
			}
			});
		
		MenuItem reportBug = new MenuItem(helpmenu, SWT.PUSH);
		reportBug.setText("Report Bugs");
		
		reportBug.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				new WebLinks("https://sourceforge.net/tracker/?group_id=162777&atid=825121");
				
			}
			});
		
		
		
		MenuItem about = new MenuItem(helpmenu, SWT.PUSH);
		
		about.setText("About");
		//about.setText("About\tCtrl+A");
		//about.setAccelerator(SWT.MOD1 + 'A');
		
		
		
		about.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("AboutScreen");
				String version = "DAISY Pipeline\n\n" +
				"Copyright [c] DAISY Consortium 2006 \n" +
				"Release One" + "\n" +
				"Framework version: " + org.daisy.dmfc.Version.getVersion() + "\n" + 
				"GUI version: " + org.daisy.dmfc.gui.Version.getVersion();
				System.out.println(version);
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION |
						SWT.CANCEL);
						messageBox.setMessage(version);
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

/*
	public MenuItem getEnableJobDetails() {
		return enableJobDetails;
	}


	public void setEnableJobDetails(MenuItem enableJobDetails) {
		this.enableJobDetails = enableJobDetails;
	}
*/

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
	
	
	public MenuItem getViewRunDetails(){
		return this.viewHideRunDetails;
	}
}

