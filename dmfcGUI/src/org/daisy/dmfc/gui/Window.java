package org.daisy.dmfc.gui;


import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.transformer.TransformerHandler;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.gui.jface.IJobListViewer;
import org.daisy.dmfc.gui.jface.JobLabelProvider;
import org.daisy.dmfc.gui.jface.JobList;
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
import org.daisy.dmfc.qmanager.LocalEventListener;
import org.daisy.dmfc.qmanager.LocalInputListener;
import org.daisy.dmfc.qmanager.Queue;
import org.daisy.dmfc.qmanager.QueueRunner;
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
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
public class Window extends Thread { 
	
	private Display display;
	private Shell shell;
	private DMFCCore dmfc;
	LocalInputListener lil;
	LocalEventListener lel;
	
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
	
	
	//Buttons
	Button btnAddSingleFile;
	Button addMultipleFiles;
	Button btnMoveUp;
	Button btnMoveDown;
	Button btnDelete;
	Button btnEdit;
	Button btnRun;
	Button btnDetails;
	Button btnTerminate;
	Button btnStart;
	
	
	
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
	Composite compJobsInQueue;
	Composite compDetails;
	
	//Layout stuff
	GridLayout layout;
	GridData data;
	
	//int 
	int index;
	
	//boolean - if jobs are being run
	public boolean executing;
	
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
		shell=new Shell (display);
		menu = new MenuDMFC(shell);
		cue=cue.getInstance();
		executing=false;
		createContents();
		shell.pack();
		
	}
	
	
	
	public void createContents(){
		
		shell.setText("Daisy Multi Format Converter");
		//shell.setMaximized(true);
		//shell.setSize(800, 600);
		
		shell.setLocation(50, 50);
		shell.setLayout(new FormLayout());
	
	//************************************************************************************
	//First Composite.  Includes selection label, selection table, 
	//browse buttons, conversion description
    //********************************************************************************
		
		Composite compSelectConversion = new Composite (shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compSelectConversion.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compSelectConversion.setLayout(layout);
		
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		this.lblSelectConversion = new Label(compSelectConversion, SWT.NONE);
		this.lblSelectConversion.setText("Select A Conversion");
		this.lblSelectConversion.setFont(FontChoices.fontLabel);
		lblSelectConversion.setLayoutData(data);
		
				
		//Composite compScriptTree = new Composite(shell, SWT.BORDER);
		//compScriptTree.setLayout(new GridLayout());
		

		//tv = new TreeViewer(compScriptTree);
		tv = new TreeViewer(compSelectConversion);
		data =  new GridData(GridData.FILL_BOTH);
		data.horizontalSpan=1;
		data.heightHint=180;
		data.widthHint=180;
		tv.getTree().setLayoutData(data);
		tv.setContentProvider(new ScriptTreeContentProvider(scriptDirectory));
		tv.setLabelProvider(new ScriptTreeLabelProvider());
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
		
		Composite addButtonsComp = new Composite(compSelectConversion, SWT.BORDER);
		layout = new GridLayout();
		layout.numColumns=1;
		layout.verticalSpacing=5;
		addButtonsComp.setLayout(layout);
		
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		//data.widthHint=120;
		this.addMultipleFiles = new Button (addButtonsComp, SWT.SHADOW_OUT);
		this.addMultipleFiles.setEnabled(false);
		addMultipleFiles.setLayoutData(data);
		
		buttonProperties.setProperties(addMultipleFiles, "Browse For Files ");
		this.addMultipleFiles.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
					getConversionSelection();
				if (scriptHandler !=null){
					getNewCMFScreen();
				}

			}
		});
		
		
		data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		//data.widthHint=120;
		this.btnAddSingleFile = new Button (addButtonsComp, SWT.SHADOW_OUT);
		this.btnAddSingleFile.setEnabled(false);
		btnAddSingleFile.setLayoutData(data);
		buttonProperties.setProperties(btnAddSingleFile, "Browse for Single File ");
		this.btnAddSingleFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getConversionSelection();
				getNewSingleFileScreen();
			}
		});
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		this.lblDescription = new Label(addButtonsComp, SWT.NONE);
		lblDescription.setText("Conversion Description");
		lblDescription.setLayoutData(data);
		
		
		
		
		data = new GridData(GridData.GRAB_VERTICAL);
		data.widthHint=125;
		data.heightHint=75;
		this.txtDescription = new Text(addButtonsComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		this.txtDescription.setBackground(ColorChoices.white);
		this.txtDescription.setEditable(false);
		txtDescription.setLayoutData(data);
	
		
		
		FormData formFill = new FormData();
		formFill.top = new FormAttachment(3, 10);
		formFill.left = new FormAttachment(5, 0);
		formFill.bottom = new FormAttachment(30, 10);
		formFill.right = new FormAttachment(65,10);
		compSelectConversion.setLayoutData(formFill);
		
		
	
		
		
	//********************************************************************
	//Second composite, located at middle left.	Includes label (jobs), 
	//table of jobs to run, and all buttons, move up, down, edit and delete
	//**********************************************************************
	
		compJobsInQueue = new Composite (shell, SWT.BORDER);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=4;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compJobsInQueue.setLayout(layout);
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 4;
		//data.heightHint=15;
		this.lblJobsInQueue2 = new Label(compJobsInQueue, SWT.NONE);
		this.lblJobsInQueue2.setText("List of all Conversion Jobs");
		//labelProperties.setProperties(lblJobsInQueue2, "List of all Conversion Jobs");
		lblJobsInQueue2.setLayoutData(data);
		
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		data.heightHint=160;
		data.widthHint=700;
		this.tblJobs2 = new Table(compJobsInQueue, SWT.BORDER |SWT.V_SCROLL | SWT.H_SCROLL |SWT.SINGLE |SWT.FULL_SELECTION );
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
				int mark = tblJobs2.getSelectionIndex();
				System.out.println("Index selected is " + mark);
				setSelectedIndex(mark);
			}
		});
	
		
	//Buttons, move up, down, delete, edit
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		this.btnMoveUp = new Button (compJobsInQueue, SWT.SHADOW_OUT);
		btnMoveUp.setLayoutData(data);
		buttonProperties.setProperties(btnMoveUp, "Move Up List");
		this.btnMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveJobUp();
				
			}
		});
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		this.btnMoveDown = new Button (compJobsInQueue, SWT.SHADOW_OUT);
		this.btnMoveDown.setLayoutData(data);
		buttonProperties.setProperties(btnMoveDown, "Move Down List");
		this.btnMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveJobDown();
				
			}
		});
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		this.btnDelete = new Button (compJobsInQueue, SWT.SHADOW_OUT);
		this.btnDelete.setLayoutData(data);
		buttonProperties.setProperties(btnDelete, "Remove from List");
		this.btnDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deleteJob();
				
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		this.btnEdit = new Button (compJobsInQueue, SWT.SHADOW_OUT);
		this.btnEdit.setLayoutData(data);
		buttonProperties.setProperties(btnEdit, "Change Selected Files");
		this.btnEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editJob();
			}
		});
				
		FormData formFill2 = new FormData();
		formFill2.top = new FormAttachment(compSelectConversion, 5);
		formFill2.left = new FormAttachment(5, 0);
		formFill2.bottom = new FormAttachment(65, 10);
		formFill2.right = new FormAttachment(65,10);
		compJobsInQueue.setLayoutData(formFill2);
		
	
		
	//*****************************************************************
	//Third Composite.  Includes, Run, Terminate, and Start over Buttons
	//******************************************************************
		
		Composite bottomComp = new Composite(shell, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		bottomComp.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=3;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		bottomComp.setLayout(layout);
		
		
		data=new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		this.btnRun = new Button(bottomComp, SWT.SHADOW_OUT);
		this.btnRun.setLayoutData(data);
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
	
		
		data=new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		this.btnTerminate = new Button(bottomComp, SWT.SHADOW_OUT);
		this.btnTerminate.setLayoutData(data);
		this.btnTerminate.setEnabled(false);
		buttonProperties.setProperties(btnTerminate, " Terminate Run");
		this.btnTerminate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				terminateJob();
			}
		});
		
		data=new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		this.btnStart = new Button(bottomComp, SWT.SHADOW_OUT);
		this.btnStart.setLayoutData(data);
		this.btnStart.setEnabled(true);
		buttonProperties.setProperties(btnStart, " Start Over ");
		this.btnStart.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				restartConverter();
			}
		});

		
		
		
		FormData formFill5 = new FormData();
		formFill5.top = new FormAttachment(compJobsInQueue, 0);
		formFill5.left = new FormAttachment(5, 0);
		formFill5.bottom = new FormAttachment(70, 10);
		formFill5.right = new FormAttachment(65,10);
		bottomComp.setLayoutData(formFill5);
		
	
	//*********************************************************************
	//Fourth Composite - includes all conversion details including
	//labels, table of transformers running, estimated time, elapsed time, 
	//conversion progress bar.  Displayed on the right side of the page next to everything else.
	//Consists of a gridLayout with 1 column
	//**********************************************************************
	
		
		compDetails= new Composite(shell, SWT.BORDER);
		compDetails.setVisible(true);
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compDetails.setLayout(layout);
	
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		//data.widthHint=160;
		data.grabExcessHorizontalSpace=true;
		this.lblConversionDetails = new Label(compDetails, SWT.NONE);
		this.lblConversionDetails.setText("Conversion Details");
		this.lblConversionDetails.setFont(FontChoices.fontLabel);
		this.lblConversionDetails.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		data.grabExcessHorizontalSpace=true;
		this.lblListTransformers = new Label(compDetails, SWT.NONE);
		lblListTransformers.setText("Current Conversion");
		lblListTransformers.setLayoutData(data);
	
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING |GridData.FILL_HORIZONTAL |GridData.VERTICAL_ALIGN_BEGINNING);
		data.grabExcessHorizontalSpace=true;
		data.horizontalSpan=2;
		this.txtConversionRunning = new Text(compDetails, SWT.BORDER);
		this.txtConversionRunning.setText("");
		this.txtConversionRunning.pack();
		//this.txtConversionRunning.s
		txtConversionRunning.setLayoutData(data);
		
		
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan=2;
		
		//first create the table
		this.tblListTransformers = new Table(compDetails, SWT.CHECK |SWT.BORDER |SWT.V_SCROLL | SWT.H_SCROLL  |SWT.FULL_SELECTION );
		TransformerListTableProperties tltp = new TransformerListTableProperties(tblListTransformers );
		tblListTransformers.setLayoutData(data);
		
		//create a tableviewer 
		createTableViewer();
		tableViewer.setContentProvider(new TransformerContentProvider());
		tableViewer.setLabelProvider(new TransformerLabelProvider());
		
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		lblElapsedTime = new Label(compDetails, SWT.NONE);
		lblElapsedTime.setText("Elapsed Time");
		lblElapsedTime.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan=1;
		txtElapsedTime = new Text(compDetails, SWT.BORDER);
		//txtElapsedTime.setText(String.valueOf(lel.getTimeLeft()));
		txtElapsedTime.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=1;
		lblEstimatedTime = new Label(compDetails, SWT.NONE);
		lblEstimatedTime.setText("Estimated Time");
		lblEstimatedTime.setLayoutData(data);
		
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan=1;
		txtEstimatedTime = new Text(compDetails, SWT.BORDER);
		//txtElapsedTime.setText(String.valueOf(lel.getTotalTime()));
		txtEstimatedTime.setLayoutData(data);
		
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		this.lblTotalConversionProgress = new Label(compDetails, SWT.NONE);
		lblTotalConversionProgress.setText("Conversion Progress");
		lblTotalConversionProgress.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan=2;
		pb = new ProgressBar(compDetails, SWT.BORDER);
		pb.setBackground(ColorChoices.white);
		pb.setMaximum(100);
		pb.setMinimum(1);
		//pb.setSelection(lel.getProgress()*100);
		pb.setLayoutData(data);
		
		FormData formFillEnd = new FormData();
		formFillEnd.top = new FormAttachment(3, 10);
		formFillEnd.left = new FormAttachment(compSelectConversion, 5);
		formFillEnd.bottom = new FormAttachment(70, 10);
		formFillEnd.right = new FormAttachment(85,10);
		compDetails.setLayoutData(formFillEnd);	
		
	}
	

	
	public void showConversionDetails(){
		TransformerListTableProperties tltp = new TransformerListTableProperties(tblListTransformers, getConversionChosen());
		tltp.populateTable();
		compDetails.setVisible(true);
		
	}
	
	
	public void open() {
		shell.open();
		
		while (!shell.isDisposed())
			if (!UIManager.display.readAndDispatch()) UIManager.display.sleep();
	}
	
	public void dispose() {
		shell.dispose();
	}
	
	
	public ScriptHandler getConversionChosen(){
		return this.scriptHandler;
	}
	
	public void getConversionDescription(){
		String description = ((ScriptHandler)hmScriptHandlers.get(fileSelectedFromTree.getName())).getDescription();
		if (description !=null){
			this.txtDescription.setText( description);
			System.out.println("Conversion description is "+ description);
		}
		else{
			this.txtDescription.setText( "");
			System.out.println("Conversion description is blank");
		}
	}
	
	
	/**
	 * Adds to Linked List, which adds to the Job Table
	 * @param job Job
	 */
	public void addToQueue(Job job){
		
		cue.addJobToQueue(job);	
		tableJobViewer.refresh();
		//jqtp2.populateTable(cue);
		//tblJobs2.redraw();	
	}

	
	
	public void terminateJob(){
		lil.setAborted(true);
		String originator = lel.getMessageOriginator();
		
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
				SWT.CANCEL);
				messageBox.setMessage("You have just terminated " + originator);
				messageBox.setText("Job Terminated");
				messageBox.open();
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
		System.out.println("the current user dir is: "+ curDir);
		
		File newScriptDir = new File(curDir+ File.separator + "scripts");
		
		//File file = new File("C:\\src\\dmfc\\doc\\examples");
		//File homeDir = dmfc.getHomeDirectory();
		//File newScriptDir = new File(homeDir.getPath()+ File.separator + "scripts");
		
		this.scriptDirectory=newScriptDir;
		System.out.println("new Script Dir " + scriptDirectory.getPath());
		
	}
	
	/**
	 * Creates a hashmap of scripthandlers 
	 * accessed by a variety of calls.
	 *
	 */
	public void createHashMapScriptHandlers(){
//		For each file in the subdirectory, create a ScriptHandler object.
		//create an hashMap to hold the script handlers
		//key = filename
		//value = scripthandler of the file
		
		File [] arrayFiles= null;
		
		System.out.println("is scriptdir set? " + scriptDirectory.getPath());
		
		
		if (scriptDirectory.isDirectory()){
			//Find list of files in directory
			arrayFiles = scriptDirectory.listFiles();
		}
		
		
		//for each directory, again list files.
		//create a scripthandler object from file (not directory)
		//add to the script handler hashmap
	
		
			for (int i = 0; i<arrayFiles.length; i++){
				File categoryDir = (File)arrayFiles[i];
				System.out.println("Name of category " + categoryDir.getName());
				
				if (categoryDir.isDirectory()){
					File []arCatFiles = categoryDir.listFiles();
					
					
					//create script handlers for each file in subdirectory
					for (int j = 0; j<arCatFiles.length; j++){
						File toSH = (File)arCatFiles[j];
						System.out.println("     Name of file in category " + toSH.getName());
						
						try{
						
							ScriptHandler sh = dmfc.createScript(toSH);
							//add to HashMap
							//key, name of file
							//value:  ScriptHandler object
							hmScriptHandlers.put(toSH.getName(), sh);
								
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
	
	
	
		

	
	
	/**
	 * called by Start Over button to restart converter
	 * clears all lists, resets the Job Queue
	 */
	public void restartConverter(){
		cue.getLinkedListJobs().clear();
		this.tableViewer.getTable().clearAll();
		this.tableJobViewer.refresh();
		this.tv.getTree().deselectAll();
		this.txtDescription.setText("");
		this.txtConversionRunning.setText("");
		this.txtElapsedTime.setText("");
		this.txtEstimatedTime.setText("");
		this.pb.setSelection(0);
		this.btnTerminate.setEnabled(false);
		this.addMultipleFiles.setEnabled(false);
		this.btnTerminate.setEnabled(false);
		this.btnRun.setEnabled(true);
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
			this.scriptHandler = (ScriptHandler)hmScriptHandlers.get(fileSelectedFromTree.getName());
		}
		
		
		
	}
	
	public Composite getCompJobsInQueue(){
		return this.compJobsInQueue;
	}
	
	public DMFCCore getDMFC(){
		return this.dmfc;
	}
	
	public LocalEventListener getLocalEventListener(){	
		return lel;
	}
	
	public boolean getExecuting(){
		return this.executing;
	}
	

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

	public Tree getTreeScriptList(){
		return this.treeScriptList;
	}
	
	public void getNewSingleFileScreen(){
		convertSingleFile = new ConvertSingleFile();
		convertSingleFile.open();
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
					messageBox.setText("Error:  Unable to Move Up List");
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
	
	public void moveJobUp(){
		if(index==0){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
					messageBox.setMessage("Cannot move the first item to a higher position.");
					messageBox.setText("Error:  Unable to Move Down List");
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
	
	public void deleteJob(){
		if(index<cue.getSizeOfQueue()){
			cue.deleteFromQueue(index);
			tableJobViewer.refresh();
			//jqtp2.populateTable(cue);
		}
		else{
			
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
					messageBox.setMessage("Cannot select a blank row in the table");
					messageBox.setText("Error:  Invalid Selection");
					messageBox.open();
		}
	}
	
	
	public void editJob(){
		
		if(index<cue.getSizeOfQueue()){
			Job job= cue.editJob(index);
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
	
	
	
	/**
	 * Runs the DMFC converter, and updates 
	 * progress of the Converter details
	 *
	 */
	public void start(){
		
		executing=true;
		
		//place long running methods in a Thread..
		execute();
		
		
		//place all methods not in event loop in own thread
		
		UIManager.display.asyncExec(new Runnable(){
			public void run(){
				
				//enable and disable buttons
				setRunTerminateButtons();
				
				
			//increment the progress bar and time remaining
				//CurrentJobDetails.getInstance().pb.setSelection((lel.getProgress()));
				pb.setSelection(lel.getProgress() * 100);
				System.out.println("lel.getProgress()" + lel.getProgress() * 100);
				
				txtElapsedTime.setText(String.valueOf(lel.getTimeLeft()));
				//CurrentJobDetails.getInstance().txtElapsedTime.setText(String.valueOf(lel.getTimeLeft()));
				System.out.println("lel.getTimeLeft()" + lel.getTimeLeft());
				
				txtEstimatedTime.setText(String.valueOf(lel.getTotalTime()));
				//CurrentJobDetails.getInstance().txtEstimatedTime.setText(String.valueOf(lel.getTotalTime()));
				System.out.println("lel.getTotalTime()" + lel.getTotalTime());
				System.out.println("getTransformerRunning() " + lel.getTransformerRunning());
				System.out.println("type" + lel.getType());	
		}
	});
		
	}
	
	
	
	public void execute(){
				
				//walk through the queue and return jobs
				LinkedList jobList = cue.getLinkedListJobs();
				
				//number in queue
				int jobNumber = 0;
				
				
				//count the transformers
				int count = -1;
				
				Iterator it = jobList.iterator();
				
				while(it.hasNext()){
					
					
					//get the Job from the Queue
					Job job = (Job)it.next();
					
					//set the name of the conversion running
					txtConversionRunning.setText(job.getScript().getName());
					scriptHandler = job.getScript();
					
					//update the transformer table
					transformerList = new TransformerList(job);
					tableViewer.setInput(transformerList);
					
					
					//add the input and output files to the script
					//actually, this only returns if the parameters are present in the script...
					scriptHandler.setProperty("input", job.getInputFile().getPath());
					scriptHandler.setProperty("outputPath", job.getOutputFile().getPath());
					
					
					
					try{	
						
					 //after the script handler is finished executing, set job to finished.
						
						scriptHandler.execute();
						int transNumber = job.getScript().getCurrentTaskIndex();
						System.out.println("what is the current task index? " + transNumber);
						count++;
						tableViewer.getTable().getItem(job.getScript().getCurrentTaskIndex()).setChecked(true);
					
					
					}
					catch(ScriptException se){
						//if the script is not valid
						//set the script in the first table to status failed.
						job.setStatus(Status.FAILED);
						tableJobViewer.refresh();
						se.getMessage();
					}
					
					//finally, reset the status in the jobs table
					//after the script has finished..
					job.setStatus(Status.COMPLETED);
					tableJobViewer.refresh();	
					
			}
		}
	
}


