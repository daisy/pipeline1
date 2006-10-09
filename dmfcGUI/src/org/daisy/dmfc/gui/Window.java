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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.EventListener;
import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.gui.core.LocalEventListener;
import org.daisy.dmfc.gui.joblist.IJobListViewer;
import org.daisy.dmfc.gui.joblist.JobLabelProvider;
import org.daisy.dmfc.gui.joblist.JobList;
import org.daisy.dmfc.gui.menus.MenuDMFC;
import org.daisy.dmfc.gui.scripttree.ScriptTreeLabelProvider;
import org.daisy.dmfc.gui.transformerlist.ITransformerListViewer;
import org.daisy.dmfc.gui.transformerlist.TransformerLabelProvider;
import org.daisy.dmfc.gui.transformerlist.TransformerList;
import org.daisy.dmfc.gui.widgetproperties.ButtonProperties;
import org.daisy.dmfc.gui.widgetproperties.ColorChoices;
import org.daisy.dmfc.gui.widgetproperties.FontChoices;
import org.daisy.dmfc.gui.widgetproperties.IProperties;
import org.daisy.dmfc.gui.widgetproperties.JobQueueTableProperties;
import org.daisy.dmfc.gui.widgetproperties.LabelProperties;
import org.daisy.dmfc.gui.widgetproperties.ListProperties;
import org.daisy.dmfc.gui.widgetproperties.TextProperties;
import org.daisy.dmfc.gui.widgetproperties.TransformerListTableProperties;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.LocalInputListener;
import org.daisy.dmfc.qmanager.Queue;
import org.daisy.dmfc.qmanager.Status;
import org.daisy.util.xml.validation.ValidationException;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;



/**
 * First screen, and singleton for the application.
 * Command center, so to speak
 * 
 * @author Laurie Sherve
 */
public class Window { 
	
	private Display display;
	private Shell shell;
	private DMFCCore dmfc;
	LocalInputListener lil;
	EventListener lel;
	
	private static Window window;
	
	//Properties 
	IProperties labelProperties = new LabelProperties();
	IProperties textProperties = new TextProperties();
	IProperties buttonProperties = new ButtonProperties();
	IProperties listProperties = new ListProperties();
	JobQueueTableProperties jqtp2;
	
	
	//Labels
	Label lblDaisyMFC;
	Label lblSelectConversion;
	Label lblJobsInQueue2;
	Label lblScriptRunning1;
	Label lblListTransformers;
	Label lblListChecked;
	Label lblElapsedTime;
	Label lblEstimatedTime;
	Label lblTotalConversionProgress;
	Label lblConversionDetails;
	Label lblDescription;
	Label lblConversionRunning;
	
	
	//Buttons
	Button btnAddSingleFile;
	Button addMultipleFiles;
	Button btnMoveUp;
	Button btnMoveDown;
	Button btnDelete;
	Button btnEdit;
	Button btnRun;
	Button btnTerminate;
	//Button btnStart;
	Button btnRemoveFinishedJobs;
	Button btnViewDetails;
	
	
	//tree of conversions
	Tree treeScriptList;
	
	//File array of conversions
	File [] arFiles ;
	File [] arScriptFiles;
	
	
	
	//Table
	public Table tblJobs2;
	Table tblListTransformers;
	
	//TextField
	Text txtScriptRunning1;
	Text txtConversionRunning;
	Text txtElapsedTime;
	Text txtEstimatedTime;
	Text txtDescription;
	
	//ProgressBar - length of running jobs
	ProgressBar pb;
	
	// Queue of all Jobs
	Queue cue;
	
	//QueueManager to run scripts
	QueueRunner queRunner;
	
	//Composite
	Composite compBigRight;
	
	Group compJobsInQueue;
	//Composite compDetails;
	Group compDetails;
	Group transformerDetails;
	
	//Layout stuff
	GridLayout layout;
	GridLayout gridLayout;  //main layout for page
	GridData data;
	
	//int 
	int index;
	int [] indices;
	
	//boolean - if jobs are being run
	public boolean executing;
	
	//Details is shown flag.  Default is true.
	boolean showRunDetails= true;
	
	//Thread to run script.execute()
	JobRunner jr;
	
	
	//Files
	File scriptDirectory;
	File fileSelectedFromTree;
	
	//HashMap
	HashMap hmScriptHandlers = new HashMap();
	
	//Array of ScriptHandler objects
	ScriptHandler [] listScriptHandlers;
	
	//The ScriptHandler to pass around
	public ScriptHandler scriptHandler;
	
	//enable items on menu
	MenuDMFC menu;
	
	//ConvertMultipleFiles screen
	ConvertMultipleFiles cmv;
	ConvertSingleFile convertSingleFile;
	LogFile logFile;
	String strViewRunDetails="";
	
	//tableViewer
	TableViewer tableViewer;
	TableViewer tableJobViewer;
	//TreeViewer
	TreeViewer tv;
	
	//transformerList
	TransformerList transformerList;
	JobList jobList = new JobList();
	
	
	//set column names
	private String[] columnNames = new String [] {"Transformers in Conversion"};
	private String [] columnJobNames = new String [] {""};
	
	public static Window getInstance(){
		if (window==null){
			window=new Window();
		}
		return window;
	}
	
	
	private Window(){
		
		//instantiates DMFCCore
		//sets the scripts home directory
		setScriptDirectory();
		
		//creates scripthandler objects for all scripts
		//and places in a hashmap
		createHashMapScriptHandlers();
		
		display=UIManager.display;
		shell=new Shell (display, SWT.CLOSE | SWT.TITLE | SWT.MIN);
		
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event e) {
				e.doit = false;
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
		
		
		
		menu = new MenuDMFC(shell);
		cue=Queue.getInstance();
		executing=false;
		
		//empty logfile contents
		deleteOldLogFile();
		
		createContents();
		createViewDetails();
	
		
		shell.pack();
	}
	
	
	/**
	 * @todo   Re-enable multi file selection button
	 * for final release.
	 *
	 */
	public void createContents(){
		
		shell.setText("Daisy Multi Format Converter");
		//shell.setMaximized(true);
		//shell.setSize(800, 600);
		
		shell.setLocation(50, 50);
		//shell.setLayout(new FormLayout());
		
		gridLayout=new GridLayout();
		gridLayout.numColumns=2;
		gridLayout.makeColumnsEqualWidth=false;
		shell.setLayout(gridLayout);
		
		//******************************************************************
		//Layout for composite on left side of the screen.  Contains
		//the first three composites./
		//*******************************************************************
		
		Composite compBigLeft = new Composite (shell, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compBigLeft.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=1;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compBigLeft.setLayout(layout);
		
		
		
		//************************************************************************************
		//First Composite.  Includes selection label, selection table, 
		//browse buttons, conversion description
		//********************************************************************************
		
		Group compSelectConversion = new Group (compBigLeft, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compSelectConversion.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compSelectConversion.setLayout(layout);
		compSelectConversion.setText("Select Converter");
		
		
		tv = new TreeViewer(compSelectConversion);
		data =  new GridData(GridData.FILL_BOTH);
		data.horizontalSpan=1;
		data.heightHint=180;
		data.widthHint=180;
		tv.getTree().setLayoutData(data);
		tv.setContentProvider(new ScriptTreeContentProvider(scriptDirectory));
		
		//ljs playing, pass the script handler hashmap to the
		//label provider to change how the file is viewed
		tv.setLabelProvider(new ScriptTreeLabelProvider(hmScriptHandlers));
		
		//tv.setLabelProvider(new ScriptTreeLabelProvider());
		tv.setInput(scriptDirectory);
		tv.getTree().deselectAll();
		tv.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				
				if(event.getSelection() instanceof IStructuredSelection && event.getSelection()!=null) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					
					File tosh = (File) selection.getFirstElement();
					if (tosh!=null){
						if (tosh.isDirectory()){
							//need or else error.  ??
						}
						else{
							btnAddSingleFile.setEnabled(true);
						
						//@todo uncomment below line for final release
						  addMultipleFiles.setEnabled(true);
							setFileSelected(tosh);
							getConversionDescription();
							
						}
					}
				}
			}
		});
		
		
		
		// This is in the second column in the compSelectConversion, first composite
		//Creates a composite to hold buttons, description, and text field
		
		
		
		
		Composite addButtonsComp = new Composite(compSelectConversion, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns=1;
		layout.verticalSpacing=5;
		addButtonsComp.setLayout(layout);
		
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		this.lblDescription = new Label(addButtonsComp, SWT.NONE);
		lblDescription.setText("Converter Description");
		lblDescription.setLayoutData(data);
		
		
		data = new GridData(GridData.GRAB_VERTICAL);
		data.widthHint= 200;
		data.heightHint= 210;
		this.txtDescription = new Text(addButtonsComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		this.txtDescription.setBackground(ColorChoices.white);
		this.txtDescription.setEditable(false);
		txtDescription.setLayoutData(data);
		
		
//		********************************************************************
		//Second group, located at middle left.	Includes label (jobs), 
		//table of jobs to run, and all buttons, move up, down, edit and delete
		//**********************************************************************
		
		compJobsInQueue = new Group (compBigLeft, SWT.NONE);
		data = new GridData();
		compJobsInQueue.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.verticalSpacing=12;
		layout.numColumns=4;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compJobsInQueue.setLayout(layout);
		
		
		compJobsInQueue.setText("Conversion Jobs");
		
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		//data.widthHint=120;
		this.btnAddSingleFile = new Button (compJobsInQueue, SWT.SHADOW_OUT);
		this.btnAddSingleFile.setEnabled(false);
		btnAddSingleFile.setLayoutData(data);
		buttonProperties.setProperties(btnAddSingleFile, "Add Single Source ");
		this.btnAddSingleFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getConversionSelection();
				if (scriptHandler !=null){
					getNewSingleFileScreen();
				}
			}
		});
		
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		//data.widthHint=120;
		this.addMultipleFiles = new Button (compJobsInQueue, SWT.SHADOW_OUT);
		
		//this is suppose to be false, and enabled once a script is chosen
		this.addMultipleFiles.setEnabled(false);
		addMultipleFiles.setLayoutData(data);
		
		buttonProperties.setProperties(addMultipleFiles, "Add Multiple Sources ");
		this.addMultipleFiles.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getConversionSelection();
				if (scriptHandler !=null){
					getNewCMFScreen();
				}
				
			}
		});
		
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		data.heightHint=160;
		data.widthHint=550;
		this.tblJobs2 = new Table(compJobsInQueue, SWT.BORDER |SWT.V_SCROLL | SWT.H_SCROLL |SWT.MULTI |SWT.FULL_SELECTION );
		this.tblJobs2.setRedraw(true);
		this.tblJobs2.setLayoutData(data);
		jqtp2 = new JobQueueTableProperties(tblJobs2);
		
		createJobTableViewer();
		tableJobViewer.setContentProvider(new JobContentProvider());
		tableJobViewer.setLabelProvider(new JobLabelProvider());
		jobList = new JobList();
		tableJobViewer.setInput(jobList);
		
		this.tblJobs2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				indices =tblJobs2.getSelectionIndices();
				if (tblJobs2.getSelectionCount()==1){
					index=tblJobs2.getSelectionIndex();
					//System.out.println("Listener in Window, what is the selection index? " + index);
				}
			}
		});
		
		
		
//		Buttons, move up, down, delete, edit
		
		data = new GridData();
		data.horizontalSpan=1;
		this.btnMoveUp = new Button (compJobsInQueue, SWT.SHADOW_OUT);
		btnMoveUp.setLayoutData(data);
		buttonProperties.setProperties(btnMoveUp, "Move Job Up");
		this.btnMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (tblJobs2.getSelectionCount()==1){
					index=tblJobs2.getSelectionIndex();
					moveJobUp();
				}
				else{
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
							SWT.CANCEL);
					messageBox.setMessage("Please select only one Job to move");
					messageBox.setText("Single Selection Required");
					messageBox.open();   
					
				}
			}
		});
		
		
		data = new GridData();
		data.horizontalSpan=1;
		this.btnMoveDown = new Button (compJobsInQueue, SWT.SHADOW_OUT);
		this.btnMoveDown.setLayoutData(data);
		buttonProperties.setProperties(btnMoveDown, "Move Job Down");
		this.btnMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (tblJobs2.getSelectionCount()==1){
					index=tblJobs2.getSelectionIndex();
					moveJobDown();
				}
				else{
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
							SWT.CANCEL);
					messageBox.setMessage("Please select only one Job to move");
					messageBox.setText("Single Selection Required");
					messageBox.open();   	
				}
				
			}
		});
		
		data = new GridData();
		data.horizontalSpan=1;
		this.btnDelete = new Button (compJobsInQueue, SWT.SHADOW_OUT);
		this.btnDelete.setLayoutData(data);
		buttonProperties.setProperties(btnDelete, "Remove Job");
		this.btnDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				indices=tblJobs2.getSelectionIndices();
				deleteJobs();
			}
		});
		data = new GridData();
		data.horizontalSpan=1;
		this.btnEdit = new Button (compJobsInQueue, SWT.SHADOW_OUT);
		this.btnEdit.setLayoutData(data);
		buttonProperties.setProperties(btnEdit, "Change Single Job");
		this.btnEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (tblJobs2.getSelectionCount()==1){
					index=tblJobs2.getSelectionIndex();
					editJob();
				}
				else{
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
							SWT.CANCEL);
					messageBox.setMessage("Please select only one Job to change");
					messageBox.setText("Single Selection Required");
					messageBox.open();   	
				}
			}
		});
		
		
		
		//*****************************************************************
		//Third Composite.  Includes, Run, View Details, Terminate Buttons
		//******************************************************************
		
		Composite bottomComp = new Composite(compBigLeft, SWT.NONE);
		//Group bottomComp = new Group(compBigLeft, SWT.NONE);
		//data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=3;
		layout.marginTop=5;
		layout.marginBottom=5;
		layout.marginWidth=7;
		bottomComp.setLayout(layout);
		bottomComp.setLayoutData(data);
		
		
		data=new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 1;
		this.btnRun = new Button(bottomComp, SWT.SHADOW_OUT);
		this.btnRun.setLayoutData(data);
		this.btnRun.setFont(FontChoices.fontButton);
		buttonProperties.setProperties(btnRun, "Run All Jobs");
		this.btnRun.addSelectionListener(new SelectionAdapter() {
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
					start();
				}
			}
		});
		
		
		
		/*
		 data= new GridData (GridData.HORIZONTAL_ALIGN_FILL);
		 data.horizontalSpan=1;
		 this.btnStart = new Button(bottomComp, SWT.SHADOW_OUT);
		 this.btnStart.setText("Start Over");
		 this.btnStart.setLayoutData(data);
		 this.btnStart.setEnabled(false);
		 this.btnStart.addSelectionListener(new SelectionAdapter(){
		 public void widgetSelected(SelectionEvent e){
		 restartConverter();
		 }
		 });
		 */
		
		data=new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 1;
		this.btnTerminate = new Button(bottomComp, SWT.SHADOW_OUT);
		this.btnTerminate.setLayoutData(data);
		//this.btnTerminate.setFont(FontChoices.fontButton);
		this.btnTerminate.setEnabled(true);
		buttonProperties.setProperties(btnTerminate, " Cancel All Jobs");
		this.btnTerminate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				terminateJob();
			}
		});
		
		
		
		data=new GridData (GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan=1;
		this.btnRemoveFinishedJobs = new Button(bottomComp, SWT.SHADOW_OUT);
		this.btnRemoveFinishedJobs.setText("Clear Finished Jobs");
		this.btnRemoveFinishedJobs.setEnabled(true);
		this.btnRemoveFinishedJobs.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected (SelectionEvent e){
				removeCompletedJobs();
			}
		});
		
		
		
		//*****************************************************
		//Composite on the right side
		//Added last composite to this so that top would
		//be aligned with the compBigLeft (without having to 
		//use pixels.)
		//*******************************************************
		
		compBigRight = new Composite (shell, SWT.NONE);
		data = new GridData(GridData.FILL_BOTH);
		compBigRight.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=1;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compBigRight.setLayout(layout);
		
	}
	//*********************************************************************
	//Fourth Group - includes all conversion details including
	//labels, table of transformers running, estimated time, elapsed time, 
	//conversion progress bar.  Displayed on the right side of the page next to everything else.
	//Consists of a gridLayout with 1 column
	//**********************************************************************
	
	public void createViewDetails(){
		
		
		compDetails= new Group(compBigRight, SWT.NONE);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint=70;
		//data.widthHint=75;
		compDetails.setLayoutData(data);
		compDetails.setVisible(true);
		layout = new GridLayout();
		layout.horizontalSpacing=10;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=0;
		layout.marginWidth=7;
		compDetails.setLayout(layout);
		compDetails.setText("Conversion Status");
		
		
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		lblElapsedTime = new Label(compDetails, SWT.NONE);
		lblElapsedTime.setText("Expected Total Transformer Time");
		lblElapsedTime.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan=2;
		txtElapsedTime = new Text(compDetails, SWT.BORDER);
		//txtElapsedTime.setText(String.valueOf(lel.getTimeLeft()));
		txtElapsedTime.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		lblEstimatedTime = new Label(compDetails, SWT.NONE);
		lblEstimatedTime.setText("Remaining Transformer Time");
		lblEstimatedTime.setLayoutData(data);
		
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan=2;
		txtEstimatedTime = new Text(compDetails, SWT.BORDER);
		//txtElapsedTime.setText(String.valueOf(lel.getTotalTime()));
		txtEstimatedTime.setLayoutData(data);
		
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		this.lblTotalConversionProgress = new Label(compDetails, SWT.NONE);
		lblTotalConversionProgress.setText("Transformer Progress");
		lblTotalConversionProgress.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan=2;
		pb = new ProgressBar(compDetails, SWT.BORDER);
		pb.setBackground(ColorChoices.white);
		pb.setMaximum(100);
		pb.setMinimum(1);
		//pb.setSelection(lel.getProgress()*100);
		pb.setLayoutData(data);
		
		data= new GridData (GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_END);
		data.horizontalSpan=2;
		this.btnViewDetails = new Button(compDetails, SWT.SHADOW_OUT);
		this.btnViewDetails.setText("Hide Run Details");
		this.btnViewDetails.setEnabled(true);
		this.btnViewDetails.setLayoutData(data);
		this.btnViewDetails.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				//check if details visible and change view
				viewRunDetails();
			}
		});
		
		createJobTransformerList();
		
	}
	
	public void createJobTransformerList(){
		
		transformerDetails= new Group(compBigRight, SWT.NONE);
		data = new GridData(GridData.FILL_BOTH);
		transformerDetails.setLayoutData(data);
		transformerDetails.setVisible(true);
		layout = new GridLayout();
		layout.horizontalSpacing=10;
		layout.numColumns=2;
		layout.marginTop=5;
		layout.marginBottom=5;
		layout.marginWidth=7;
		transformerDetails.setLayout(layout);
		transformerDetails.setText("Current Conversion");
		
	
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING |GridData.FILL_HORIZONTAL |GridData.VERTICAL_ALIGN_BEGINNING);
		data.grabExcessHorizontalSpace=true;
		data.horizontalSpan=2;
		
		this.lblConversionRunning = new Label(transformerDetails, SWT.BORDER);
		this.lblConversionRunning.setText("");
		this.lblConversionRunning.pack();
		lblConversionRunning.setLayoutData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan=2;
		
		//first create the table
		this.tblListTransformers = new Table(transformerDetails, SWT.CHECK |SWT.BORDER |SWT.V_SCROLL | SWT.H_SCROLL  |SWT.FULL_SELECTION );
		TransformerListTableProperties tltp = new TransformerListTableProperties(tblListTransformers );
		tblListTransformers.setLayoutData(data);
		
		//create a tableviewer 
		createTableViewer();
		tableViewer.setContentProvider(new TransformerContentProvider());
		tableViewer.setLabelProvider(new TransformerLabelProvider());
			
	}
	
	
	
	public void open() {
		shell.open();
		
		
		
		while (!shell.isDisposed()){
			if (!UIManager.display.readAndDispatch()) 
				
				
				
				UIManager.display.sleep();
			
		}
		
		
		
	
	}
	
	public void dispose() {
		
		
		shell.dispose();
	}
	
	
	
	//*******************************************************
	//Conversion/ScriptHandler 
	
	public ScriptHandler getConversionChosen(){
		return this.scriptHandler;
	}
	
	public void getConversionDescription(){
		String description = ((ScriptHandler)hmScriptHandlers.get(fileSelectedFromTree.getPath())).getDescription();
		if (description !=null){
			this.txtDescription.setText( description);
			//System.out.println("Conversion description is "+ description);
		}
		else{
			this.txtDescription.setText( "");
			//System.out.println("Conversion description is blank");
		}
	}
	
	
	/**
	 * Adds to Linked List, which adds to the Job Table
	 * @param job Job
	 */
	public void addToQueue(Job job){
		cue.addJobToQueue(job);	
		tableJobViewer.refresh();	
	}
	
	/**
	 * terminates the job? Transformer?
	 *
	 */
	public void terminateJob(){
		
		if(this.executing==true){
		
			((LocalInputListener)lil).setAborted(true);
			String originator = ((LocalEventListener)lel).getMessageOriginator();
			
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("You have just terminated " + originator);
			messageBox.setText("Job Terminated");
			messageBox.open();   
			((LocalEventListener)lel).getJob().setStatus(Status.FAILED);
			tableJobViewer.refresh();
			this.btnRun.setEnabled(true);
		}
		
		else{
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("No Conversion is in progress.");
			messageBox.setText("Error:  No Conversion in Progress");
			messageBox.open();
		}
	}
	
	
	/**
	 * 
	 * instantiates dmfc core
	 * sets home directory for scripts
	 * @return
	 */
	public void setScriptDirectory(){
		
		lil = new LocalInputListener();
		lel = new LocalEventListener();
		try {
			dmfc = new DMFCCore(lil, lel);
		} catch (DMFCConfigurationException e) {
			e.printStackTrace();
		}
		
		String curDir = System.getProperty("user.dir");
		//System.out.println("the current user dir is: "+ curDir);
		File newScriptDir = new File(curDir+ File.separator + "scripts");
		
		this.scriptDirectory=newScriptDir;
		//System.out.println("new Script Dir " + scriptDirectory.getPath());
		
	}
	
	/**
	 * Creates a hashmap of scripthandlers 
	 * accessed by a variety of calls.
	 *
	 */
	public void createHashMapScriptHandlers(){
		//mg: this is not recursive, ie only allows one level subdirs
		//nor supports scripts as direct descendants of main scriptdir
		//remember that users may add their own scrips so this needs improvement
		//if we dont need the categories sort here, we can just use EFolder		
		//Collection scripts = scriptDirAsEFolder.getFiles((deep=true, ".*\.xml", false); 
		//also to avoid getting most of those 999 exceptions from createScript in developer mode
		//when the file is not a script
		
		
//		For each file in the subdirectory, create a ScriptHandler object.
		//create an hashMap to hold the script handlers
		//key = filename
		//value = scripthandler of the file
		
		File [] arrayFiles= null;
		
		//System.out.println("is scriptdir set? " + scriptDirectory.getPath());
		
		if (scriptDirectory.isDirectory()){
			//Find list of files in directory
			arrayFiles = scriptDirectory.listFiles();
		}
		//for each directory, again list files.
		//create a scripthandler object from file (not directory)
		//add to the script handler hashmap
		
		for (int i = 0; i<arrayFiles.length; i++){
			File categoryDir = (File)arrayFiles[i];
			//System.out.println("Name of category " + categoryDir.getName());
			
			if (categoryDir.isDirectory()){
				File []arCatFiles = categoryDir.listFiles();				
				
				//create script handlers for each file in subdirectory
				for (int j = 0; j<arCatFiles.length; j++){
					File toSH = (File)arCatFiles[j];
					//System.out.println("     Name of file in category " + toSH.getName());
					
					//mg: all System.out.println statements should be surrounded by a 
					//debug test clause, will make the app snappier 
					//I have started using:
					//if(System.getProperty("org.daisy.debug")!=null) {
					//	System.out.println("blah");
					//}
					//so its easily switched on/off without excessive code
					
					try{
						
						ScriptHandler sh = dmfc.createScript(toSH);
						//add to HashMap
						//key, name of file
						//value:  ScriptHandler object
						hmScriptHandlers.put(toSH.getPath(), sh);
						
					}
					catch(ValidationException ve){
						ve.getMessage();
						ve.printStackTrace();
					}
					catch(MIMEException me){
						me.getMessage();
						me.printStackTrace();
					}
					catch(ScriptException se){
						//add error messages to be thrown to GUI
						se.printStackTrace();
					}	
				}
			}
		}	
	}
	
	
	
	
	public void viewRunDetails(){
		UIManager.display.asyncExec(new Runnable(){
			public void run(){
				if (btnViewDetails.getText().equalsIgnoreCase("View Run Details")){
					btnViewDetails.setText("Hide Run Details");
					strViewRunDetails="Hide Run Details";
					menu.getViewRunDetails().setText(strViewRunDetails);
					transformerDetails.setVisible(true);
				}
				else{
					btnViewDetails.setText("View Run Details");
					strViewRunDetails="View Run Details";
					menu.getViewRunDetails().setText(strViewRunDetails);
					transformerDetails.setVisible(false);	
				}
			}
		});
	}
	
	public String getStrViewRunDetails(){
		return this.strViewRunDetails;
	}
	
	
	
	/**
	 * called by Start Over button to restart converter
	 * clears all lists, resets the Job Queue
	 * Not used, button removed, functionality remains in code
	 */
	public void restartConverter(){
		cue.getLinkedListJobs().clear();
		this.tableViewer.getTable().clearAll();
		this.tableJobViewer.refresh();
		this.tv.getTree().deselectAll();
		this.txtDescription.setText("");
		//this.txtConversionRunning.setText("");
		this.lblConversionRunning.setText("");
		this.txtElapsedTime.setText("");
		this.txtEstimatedTime.setText("");
		this.pb.setSelection(0);
		this.btnTerminate.setEnabled(false);
		this.addMultipleFiles.setEnabled(false);
		this.btnTerminate.setEnabled(false);
		this.btnRun.setEnabled(true);
		//this.btnRemoveFinishedJobs.setEnabled(false);
	}
	
	/**
	 * Removed completed jobs from Queue
	 *
	 */
	public void removeCompletedJobs(){
		
		ArrayList alJob = new ArrayList();
		
		int size = cue.getLinkedListJobs().size();
		for (int i =0; i<size;i++){
			Job job = (Job)cue.getLinkedListJobs().get(i);
			int status = job.getStatus();
			
			//status = completed or failed
			if (status==Status.COMPLETED || status == Status.FAILED){
				System.out.println("Delete failed or completed from cue, input: " + job.getInputFile() + " index of " + i);

				cue.getLinkedListJobs().remove(job);
				alJob.add(job);
			}
		}
		
		//cue.getLinkedListJobs().removeAll(alJob);
		//cue.getLinkedListJobs().
		
		
		tableJobViewer.refresh();
		btnRun.setEnabled(true);
		clearRunDetails();
	}
	
	public void clearRunDetails(){
		this.lblConversionRunning.setText("");
		this.txtElapsedTime.setText("");
		this.txtEstimatedTime.setText("");
		this.pb.setSelection(0);
		this.tableViewer.getTable().clearAll();
	}
	
	/**
	 * sets the file selected from the tree to be passed to the 
	 * create scripthandler method.
	 * @param file
	 */
	public void setFileSelected(File file){
		fileSelectedFromTree = file;
	}
	
	
	
	public void getConversionSelection(){
		//create a scripthandler object from the file selected in the tree.
		
		if( fileSelectedFromTree==null || fileSelectedFromTree.isDirectory()){	
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("Please select a file, not a directory");
			messageBox.setText("Error:  Wrong Type Selection");
			messageBox.open();
		}
		else{
			this.scriptHandler = (ScriptHandler)hmScriptHandlers.get(fileSelectedFromTree.getPath());
		}
		
	}
	
	
	
	public Composite getCompJobsInQueue(){
		return this.compJobsInQueue;
	}
	
	public DMFCCore getDMFC(){
		return this.dmfc;
	}
	
	public LocalEventListener getLocalEventListener(){	
		return (LocalEventListener)lel;
	}
	
	public boolean getExecuting(){
		return this.executing;
	}
	
	//*****************************************************
	//other screens, called from listeners and menus
	
	public ConvertMultipleFiles getConvertMultipleFiles(){
		return cmv;	
	}
	
	public void getNewCMFScreen(){
		
		cmv = new ConvertMultipleFiles();
		cmv.open();
	}
	
	public ConvertSingleFile getConvertSingleFile(){
		return convertSingleFile;	
	}
	
	public void getNewSingleFileScreen(){
		convertSingleFile = new ConvertSingleFile();
		//convertSingleFile = new ConvertSingleFile(dmfc);
		convertSingleFile.open();
	}
	
	public String getLogFileContents(){
		String logFileContents="";
		//String logFileName = System.getProperty("user.dir")+ File.separator + "dmfc_lastrun.log";
		String logFileName = System.getProperty("user.dir")+ File.separator + "logFile.txt";
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(logFileName));
			String str;
			while ((str = in.readLine()) != null) {
				logFileContents = logFileContents+ str + "\n";
			}
			in.close();
		} catch (IOException e) {
		}
		return logFileContents;
	}
	
	public void getLogFile(){
		UIManager.display.asyncExec(new Runnable(){
			public void run(){
				logFile = new LogFile(getLogFileContents());
				logFile.open();
			}
		});
	}
	
	public void deleteOldLogFile(){
		String logFileName = System.getProperty("user.dir")+ File.separator + "logFile.txt";
		
	    boolean success = (new File(logFileName)).delete();
	    if (!success) {
	        System.out.println("It's going to be a long file");
	    }
	    else{
	    	System.out.println("Log file deleted");
	    }


		
	}
	
	//*****************************************
	
	
	public Tree getTreeFromTreeViewer(){
		return this.tv.getTree();
	}
	
	
	public void setRunTerminateButtons(){
		this.btnTerminate.setEnabled(true);
		this.btnRun.setEnabled(false);
		
	}
	
	/**
	 * 
	 * @param mark int, item number in the list selected from the jobs table
	 */
	public void setSelectedIndex(int mark){
		this.index=mark;
	}
	
	public void moveJobDown(){
		if(index==cue.getSizeOfQueue()-1){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("Cannot move the last item to a lower position.");
			messageBox.setText("Error:  Unable to Move DOWN List");
			messageBox.open();
		}
		else if(index<cue.getSizeOfQueue()){
			cue.moveDown(index);
			tableJobViewer.refresh();
		}
		
		else{	
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("Cannot select a blank row in the table");
			messageBox.setText("Error:  Invalid Selection");
			messageBox.open();
		}
	}
	
	public void moveJobUp(){
		if(index==0){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("Cannot move the first item to a higher position.");
			messageBox.setText("Error:  Unable to Move UP List");
			messageBox.open();
		}
		else if(index<cue.getSizeOfQueue()){
			cue.moveUp(index);
			tableJobViewer.refresh();
			
		}
		
		else{	
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("Cannot select a blank row in the table");
			messageBox.setText("Error:  Invalid Selection");
			messageBox.open();
		}
	}
	
	
	
	
	public void deleteJobs(){
		int size=indices.length;
		//System.out.println("size of selection is " + size);
		//System.out.println("size of job que: " + cue.getSizeOfQueue());
		
		for (int i=indices.length-1; i>-1;i--){
			System.out.println("the values are " + indices[i]);
			deleteJob(indices[i]);
			tableJobViewer.refresh();
		}
		tableJobViewer.refresh();
		this.viewJobList();
	}
	
	public void deleteJob(int mark){

		if(mark<cue.getSizeOfQueue()){
			cue.deleteFromQueue(mark);
			tableJobViewer.refresh();
			
		}
		else{
			
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("Cannot select a blank row in the table");
			messageBox.setText("Error:  Invalid Selection");
			messageBox.open();
		}
		tableJobViewer.refresh();
	}
	
	
	public void editJob(){
		
		if(index<cue.getSizeOfQueue()){
			Job job= cue.editJob(index);
			System.out.println("Edit job, what is the index? " + index);
			cue.deleteFromQueue(index);
			ConvertSingleFile csf = new ConvertSingleFile();
			csf.editConversion(job);
			csf.open();	
		}
		
		else{
			
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("Cannot select a blank row in the table");
			messageBox.setText("Error:  Invalid Selection");
			messageBox.open();
		}
	}
	
	
	/**
	 * create viewer and place an editor on the checkbox column
	 *
	 */
	public void createTableViewer(){
		tableViewer = new TableViewer(tblListTransformers);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(columnNames);
		
		CellEditor []editors = new CellEditor[columnNames.length];
		editors[0]= new CheckboxCellEditor(tblListTransformers);
		
		//assign the cell editors to the viewer
		tableViewer.setCellEditors(editors);
		
	}
	
	public void createJobTableViewer(){
		tableJobViewer = new TableViewer(tblJobs2);
		tableJobViewer.setUseHashlookup(true);
		tableJobViewer.setColumnProperties(columnJobNames);
		
	}
	
	
	/**
	 * InnerClass that acts as a proxy for the TransformerList 
	 * providing content for the Table. It implements the ITransformerListViewer 
	 * interface since it must register changeListeners with the 
	 * TransformerList 
	 */
	class TransformerContentProvider implements IStructuredContentProvider, ITransformerListViewer {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((TransformerList) newInput).addChangeListener(this);
			if (oldInput != null)
				((TransformerList) oldInput).removeChangeListener(this);
		}
		
		public void dispose() {
		}
		
		// Return the transformers as an array of Objects
		public Object[] getElements(Object parent) {
			if (transformerList == null) {
				System.err.println("transformerList is null!");
				return new Object[]{};
			}
			return transformerList.getTransformers().toArray();
		}
		
		/* 
		 */
		public void addTransformer(TransformerHandler th) {
			tableViewer.add(th);
		}
		
		/* 
		 */
		public void removeTransformer(TransformerHandler th) {
			tableViewer.remove(th);			
		}
		
		/* 
		 */
		public void updateTransformer(TransformerHandler th) {
			tableViewer.update(th, null);	
		}
	}
	
	
	/**
	 * Provides the content for  the job table.
	 * It implements the IJobListViewer 
	 * interface since it must register changeListeners with the 
	 * JobList 
	 */
	class JobContentProvider implements IStructuredContentProvider, IJobListViewer {
		
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((JobList) newInput).addChangeListener(this);
			if (oldInput != null)
				((JobList) oldInput).removeChangeListener(this);
		}
		
		public void dispose() {
			jobList.removeChangeListener(this);
		}
		
		// Return the jobs as an array of Objects
		public Object[] getElements(Object parent) {
			return jobList.getJobs().toArray();
		}
		
		/* 
		 * 
		 */
		public void addJob(Job job) {
			tableJobViewer.add(job);
		}
		
		/* (
		 * 
		 */
		public void removeJob(Job job) {
			tableJobViewer.remove(job);			
		}
		
		/* (
		 * 
		 */
		public void updateJob(Job job) {
			tableJobViewer.update(job, null);	
		}
	}
	
	
	/**
	 * Provides the content for  scripts tree
	 * It implements the ITreeContentProvider
	 * 
	 */
	class ScriptTreeContentProvider implements ITreeContentProvider {
		
		File scriptDirectory;
		
		public ScriptTreeContentProvider (File file){
			scriptDirectory=file;
		}
		
		/**
		 * Gets children of specified object
		 * @param arg0 - the parent object
		 * @return Object[]
		 */
		public Object [] getChildren(Object arg0){
			//Returns the files and subdirectories in this directory
			return ((File) arg0).listFiles();
		}
		
		/**
		 * Gets parent of object
		 * @param Object the object with a parent
		 * @return Object
		 */
		public Object getParent(Object arg0){
			return ((File)arg0).getParentFile();
		}
		
		/**
		 * returns if object has children
		 */
		public boolean hasChildren (Object arg0){
			
			Object [] obj = getChildren(arg0);
			return obj==null ? false: obj.length>0;
		}
		
		/**
		 * gets the root elements of the tree
		 *
		 */
		public Object[] getElements(Object arg0){
			return scriptDirectory.listFiles();
			
		}
		
		public void dispose(){
			//nothing to dispose
		}
		
		/**
		 * Called when input changes
		 */
		public void inputChanged (Viewer viewer, Object obj1, Object obj2){
			//nothing to change, in the file system
		}
		
	}
	
	public void viewJobList(){
		int size = cue.getLinkedListJobs().size();
		for (int i=0; i<size; i++){
			System.out.println("The input file is: " + ((Job)cue.getLinkedListJobs().get(i)).getInputFile());
		}
	}
	
	
	
	/**
	 * Runs the DMFC converter, and updates 
	 * progress of the Converter details
	 *
	 */
	public void start(){
		
		// Clear abort flag
		lil.setAborted(false);
		
		//enable and disable buttons
		setRunTerminateButtons();
		
		//pass the widgets to the event listener
		this.getLocalEventListener().setAttributes(txtElapsedTime, txtEstimatedTime, pb, tableViewer);
		
		execution();
	}
	
	
	
	
	
	public void execution(){
//		walk through the queue and return jobs
		LinkedList jobList = cue.getLinkedListJobs();
		
		QueueRunner queueRunner = new QueueRunner(jobList, shell);
		queueRunner.start();
		
	}
	
}