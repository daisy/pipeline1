package org.daisy.dmfc.gui;


import java.io.File;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.exception.DMFCConfigurationException;
import org.daisy.dmfc.exception.MIMEException;
import org.daisy.dmfc.exception.ScriptException;
import org.daisy.dmfc.gui.menus.MenuDMFC;
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
import org.daisy.util.xml.validation.ValidationException;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;



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
	
	
	//Buttons
	//Button addSingleFile;
	Button addMultipleFiles;
	Button btnMoveUp;
	Button btnMoveDown;
	Button btnDelete;
	Button btnEdit;
	Button btnRun;
	Button btnDetails;
	Button btnTerminate;
	
	//Lists
	public List listConversion;
	
	//File array of conversions
	File [] arFiles ;
	
	//Table
	public Table tblJobs2;
	Table tblListTransformers;
	
	//TextField
	Text txtScriptRunning1;
	Text txtConversionRunning;
	Text txtElapsedTime;
	Text txtEstimatedTime;
	
	//ProgressBar - length of running jobs
	ProgressBar pb;
	
	// Queue of all Jobs
	Queue cue;
	
	//QueueManager to run scripts
	QueueRunner queRunner;
	
	//Composite
	Composite compJobsInQueue;
	Composite compDetails;
	
	//int 
	int index;
	
	//boolean - if jobs are being run
	public boolean executing;
	
	//Array of ScriptHandler objects
	ScriptHandler [] listScriptHandlers;

	//The ScriptHandler to pass around
	ScriptHandler scriptHandler;
	
	//enable items on menu
	MenuDMFC menu;
	
	//ConvertMultipleFiles screen
	ConvertMultipleFiles cmv;
	
	
	public static Window getInstance(){
		if (window==null){
			window=new Window();
		}
		return window;
	}

	
	private Window(){
		
		display=UIManager.display;
		shell=new Shell (display);
		shell.setBackground(ColorChoices.white);
		menu = new MenuDMFC(shell);
		cue=cue.getInstance();
		executing=false;
		createContents();
		shell.pack();
		
	}
	
	
/*
	private Window() {
		this(new Shell(UIManager.display));
	}
	
	
	private Window(final Shell shell) {
		super(shell, SWT.V_SCROLL);
		this.shell = shell;
		shell.setBackground(ColorChoices.white);
		menu = new MenuDMFC(shell);
		
		cue=cue.getInstance();
		executing=false;
		createContents();
		shell.pack();	
	}
	
*/
	
	
	public void createContents(){
		
		shell.setText("Daisy Multi Format Converter");
		shell.setMaximized(true);
		shell.setBackground(ColorChoices.white);
		shell.setLayout(new FormLayout());
	
	//SOFTWARE TITLE
		
		Label lblDaisyMFC = new Label(shell, SWT.NONE);
		lblDaisyMFC.setForeground(ColorChoices.darkBlue);
		lblDaisyMFC.setText("DAISY Multi-Format Converter");
		lblDaisyMFC.setFont(FontChoices.fontTitle);
		lblDaisyMFC.setBackground(ColorChoices.white);
				
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 20);
		formData.left = new FormAttachment(20, 20);
		formData.bottom = new FormAttachment(8, 10);
		formData.right = new FormAttachment(65,10);
		lblDaisyMFC.setLayoutData(formData);
		
	//Top Left
		
		Composite compSelectConversion = new Composite (shell, SWT.NONE);
		compSelectConversion.setBackground(ColorChoices.white);
		RowLayout rowLayout3 = new RowLayout(SWT.VERTICAL);
		rowLayout3.pack = true;
		rowLayout3.spacing = 15;
		compSelectConversion.setLayout(rowLayout3);
		
		this.lblSelectConversion = new Label(compSelectConversion, SWT.NONE);
		labelProperties.setProperties(lblSelectConversion, "Select A Conversion");
		
		FormData formFill3 = new FormData();
		formFill3.top = new FormAttachment(8, 20);
		formFill3.left = new FormAttachment(0, 20);
		formFill3.bottom = new FormAttachment(13, 10);
		formFill3.right = new FormAttachment(25,10);
		compSelectConversion.setLayoutData(formFill3);
		
		
		//Just the list, ma'am
		this.listConversion= new List(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		listProperties.setProperties(listConversion, "Select Conversion Options(s)");
		populateList();
		
		this.listConversion.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int selection = listConversion.getSelectionCount();
				//System.out.println ("The number selected is " + selection);
				if (selection==1){
					addMultipleFiles.setEnabled(true);	
				}
			}
		});
		
		
		FormData form = new FormData();
		form.top = new FormAttachment(12, 20);
		form.left = new FormAttachment(0, 20);
		form.bottom = new FormAttachment(28, 10);
		form.right = new FormAttachment(25,10);
		listConversion.setLayoutData(form);
		
//Create Buttons on the right top side of the screen	
		
		Composite addButtonsComp = new Composite(shell, SWT.NONE);
		addButtonsComp.setBackground(ColorChoices.white);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.pack = false;
		rowLayout.spacing = 10;
		addButtonsComp.setLayout(rowLayout);
		
		
		this.addMultipleFiles = new Button (addButtonsComp, SWT.SHADOW_OUT);
		this.addMultipleFiles.setEnabled(false);
		
		buttonProperties.setProperties(addMultipleFiles, "Browse For Files ");
		this.addMultipleFiles.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getConversionSelection();
				getNewCMFScreen();

			}
		});
		
		
		FormData formFill = new FormData();
		formFill.top = new FormAttachment(12, 20);
		formFill.left = new FormAttachment(compSelectConversion, 15);
		formFill.bottom = new FormAttachment(28, 10);
		formFill.right = new FormAttachment(45,10);
		addButtonsComp.setLayoutData(formFill);
		
	//Bottom left
		compJobsInQueue = new Composite (shell, SWT.NONE);
		compJobsInQueue.setBackground(ColorChoices.white);
		/*RowLayout rowLayout4 = new RowLayout(SWT.VERTICAL);
		rowLayout4.pack = true;
		rowLayout4.spacing = 15;
		compJobsInQueue.setLayout(rowLayout4);
		*/
		
		GridLayout gridLayoutJobs = new GridLayout();
		compJobsInQueue.setLayout(gridLayoutJobs);
		
		
		GridData data = new GridData();
		data.heightHint=15;
		this.lblJobsInQueue2 = new Label(compJobsInQueue, SWT.NONE);
		labelProperties.setProperties(lblJobsInQueue2, "List of all Conversion Jobs");
		lblJobsInQueue2.setLayoutData(data);
		
		
		data = new GridData();
		data.heightHint=120;
		this.tblJobs2 = new Table(compJobsInQueue, SWT.BORDER |SWT.V_SCROLL | SWT.H_SCROLL |SWT.SINGLE |SWT.FULL_SELECTION );
		this.tblJobs2.setRedraw(true);
		jqtp2 = new JobQueueTableProperties(tblJobs2);
		tblJobs2.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				int mark = tblJobs2.getSelectionIndex();
				System.out.println("Index selected is " + mark);
				setSelectedIndex(mark);
			}
		});
		tblJobs2.setLayoutData(data);
		
		FormData formFill4 = new FormData();
		formFill4.top = new FormAttachment(listConversion, 15);
		formFill4.left = new FormAttachment(0, 20);
		formFill4.bottom = new FormAttachment(55, 10);
		formFill4.right = new FormAttachment(70,10);
		compJobsInQueue.setLayoutData(formFill4);
		
		
//	Buttons on right bottom of screen
		Composite moveJobsComp = new Composite(shell, SWT.NONE);
		moveJobsComp.setBackground(ColorChoices.white);
		RowLayout rowLayout2 = new RowLayout(SWT.VERTICAL);
		rowLayout2.pack = false;
		rowLayout2.spacing = 10;
		moveJobsComp.setLayout(rowLayout2);
		
		this.btnMoveUp = new Button (moveJobsComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnMoveUp, "Move Up List");
		this.btnMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveJobUp();
				jqtp2.populateTable(cue);
			}
		});
		
		
		this.btnMoveDown = new Button (moveJobsComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnMoveDown, "Move Down List");
		this.btnMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveJobDown();
				jqtp2.populateTable(cue);
			}
		});
		
		
		this.btnDelete = new Button (moveJobsComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnDelete, "Remove from List");
		this.btnDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deleteJob();
				jqtp2.populateTable(cue);
			}
		});
		
		this.btnEdit = new Button (moveJobsComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnEdit, "Change Selected Files");
		this.btnEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editJob();
			}
		});
		
		
		FormData formFill2 = new FormData();
		formFill2.top = new FormAttachment(36, 20);
		formFill2.left = new FormAttachment(compJobsInQueue, 10);
		formFill2.bottom = new FormAttachment(63, 10);
		formFill2.right = new FormAttachment(85,10);
		moveJobsComp.setLayoutData(formFill2);
		
		
	//Bottom Buttons
		
		Composite bottomComp = new Composite(shell, SWT.NONE);
		
		bottomComp.setBackground(ColorChoices.white);
		RowLayout rowLayout5 = new RowLayout(SWT.HORIZONTAL);
		rowLayout5.pack = false;
		rowLayout5.spacing = 20;
		bottomComp.setLayout(rowLayout5);
		
		this.btnRun = new Button(bottomComp, SWT.SHADOW_OUT);
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
					runScript();
				}
			}
		});
		

		this.btnDetails = new Button(bottomComp, SWT.SHADOW_OUT);
		this.btnDetails.setEnabled(false);
		buttonProperties.setProperties(btnDetails, " Conversion Details");
		this.btnDetails.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//CurrentJobDetails.getInstance().open();
				showConversionDetails();
			}
		});
		
		
		
		this.btnTerminate = new Button(bottomComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnTerminate, " Terminate Run");
		
		
		FormData formFill5 = new FormData();
		formFill5.top = new FormAttachment(compJobsInQueue, 35);
		formFill5.left = new FormAttachment(0, 20);
		formFill5.bottom = new FormAttachment(56, 100);
		formFill5.right = new FormAttachment(55,10);
		bottomComp.setLayoutData(formFill5);
		

	//Conversion Details, shown only if asked for
		
		compDetails= new Composite(shell, SWT.V_SCROLL);
		compDetails.setVisible(false);
		
		GridLayout gridlayout = new GridLayout(4, false);
		//gridlayout.verticalSpacing=20;
		gridlayout.horizontalSpacing=17;
		gridlayout.marginHeight=5;
		
		
		compDetails.setLayout(gridlayout);
	
		
		
	//First column
		Composite compColumnOne = new Composite(compDetails, SWT.BORDER);
		GridLayout gridColumnOne = new GridLayout();
		gridColumnOne.verticalSpacing=20;
		gridColumnOne.horizontalSpacing=20;
		compColumnOne.setLayout(gridColumnOne);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.widthHint=160;
		data.grabExcessHorizontalSpace=true;
		this.lblListTransformers = new Label(compColumnOne, SWT.NONE);
		lblListTransformers.setText("Current Conversion");
		lblListTransformers.setLayoutData(data);
		//labelProperties.setProperties(lblListTransformers, "Current Conversion");
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING |GridData.FILL_HORIZONTAL |GridData.VERTICAL_ALIGN_BEGINNING);
		data.grabExcessHorizontalSpace=true;
		this.txtConversionRunning = new Text(compColumnOne, SWT.BORDER);
		this.txtConversionRunning.setText("");
		this.txtConversionRunning.pack();
		//this.txtConversionRunning.s
		txtConversionRunning.setLayoutData(data);
		
		
	//Second Column
		Composite compColumnTwo = new Composite(compDetails, SWT.BORDER);
		GridLayout gridColumnTwo = new GridLayout();
		gridColumnTwo.verticalSpacing=10;
		gridColumnTwo.horizontalSpacing=20;
		compColumnTwo.setLayout(gridColumnOne);
		
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.widthHint=270;
		this.lblListChecked = new Label(compColumnTwo, SWT.NONE);
		lblListChecked.setText("Transformers in Conversion");
		lblListChecked.setLayoutData(data);
		
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint=70;
		this.tblListTransformers = new Table(compColumnTwo, SWT.CHECK |SWT.BORDER |SWT.V_SCROLL | SWT.H_SCROLL  |SWT.FULL_SELECTION );
		
		tblListTransformers.setLayoutData(data);
		
		
	//Third column
		Composite compColumnThree = new Composite(compDetails, SWT.BORDER);
		GridLayout gridColumnThree = new GridLayout();
		compColumnThree.setLayout(gridColumnThree);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		lblElapsedTime = new Label(compColumnThree, SWT.NONE);
		lblElapsedTime.setText("Elapsed Time");
		lblElapsedTime.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		lblEstimatedTime = new Label(compColumnThree, SWT.NONE);
		lblEstimatedTime.setText("Estimated Time");
		lblEstimatedTime.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		this.lblTotalConversionProgress = new Label(compColumnThree, SWT.NONE);
		lblTotalConversionProgress.setText("Conversion Progress");
		lblTotalConversionProgress.setLayoutData(data);
		
		
	//Fourth column
		
		Composite compColumnFour = new Composite(compDetails, SWT.BORDER);
		GridLayout gridColumnFour = new GridLayout();
		compColumnFour.setLayout(gridColumnFour);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		txtElapsedTime = new Text(compColumnFour, SWT.NONE);
		txtElapsedTime.setText(String.valueOf(lel.getTimeLeft()));
		txtElapsedTime.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		txtEstimatedTime = new Text(compColumnFour, SWT.NONE);
		txtElapsedTime.setText(String.valueOf(lel.getTotalTime()));
		txtEstimatedTime.setLayoutData(data);
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		pb = new ProgressBar(compColumnFour, SWT.NONE);
		pb.setSelection(lel.getProgress());
		pb.setLayoutData(data);
		
		
		
		FormData formFillEnd = new FormData();
		formFillEnd.top = new FormAttachment(bottomComp, 0);
		formFillEnd.left = new FormAttachment(0, 20);
		formFillEnd.bottom = new FormAttachment(80, 100);
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
	
	
	/**
	 * Adds to Linked List, which adds to the Job Table
	 * @param job Job
	 */
	public void addToQueue(Job job){
		
		cue.addJobToQueue(job);		
		jqtp2.populateTable(cue);
		tblJobs2.redraw();
	
	}

	
	
	/**
	 * Clear all fields in the gui
	 */
	
	public void clearFields(){
		this.clearFields();
		tblJobs2.clearAll();
	}
	
	/**
	 * Creates ScriptHandler objects
	 * From files on the file system.
	 * These ScriptHandler Objects are used in the conversions
	 * All attributes of a script, the name, description, parameters, etc
	 * can be found in this object.  The object must be passed 
	 * among the screens, and the description taken for each item,
	 * the mime types used taken for each script, etc.
	 *  @todo - add the file location to the properties file? At least 
	 *  take out the hardcoding and parameterize.
	 */
	public void populateList(){
		 lil = new LocalInputListener();
		 lel = new LocalEventListener();
		try {
			dmfc = new DMFCCore(lil, lel);
		} catch (DMFCConfigurationException e) {
			e.printStackTrace();
		}
		//File file = new File("C:\\src\\dmfc\\doc\\examples");
	//	File homeDir = dmfc.getHomeDirectory();
		//System.out.println("The home directory is: " + homeDir.getAbsolutePath());

		File file = new File("C:\\src\\dmfcgui\\src\\scripts");
		if (file.isDirectory()){
			arFiles = file.listFiles();
			
			//For each file in the directory, create a ScriptHandler object.
			listScriptHandlers= new ScriptHandler[arFiles.length];
			
			for (int i = 0; i <arFiles.length; i++){
				//listConversion.add( arFiles[i].getName());
				File toSH = (File)arFiles[i];
				
				try{
				
				ScriptHandler sh = dmfc.createScript(toSH);
							
				listScriptHandlers[i]=sh;
				listConversion.add(listScriptHandlers[i].getName());
				
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
	
	public void getConversionSelection(){
		if(listConversion.getSelectionCount()==1){
			
			//get the script from the list
			int focus = listConversion.getFocusIndex();
			this.scriptHandler= listScriptHandlers[focus];
			
			
		}
	}
	
	public Composite getCompJobsInQueue(){
		return this.compJobsInQueue;
	}
	
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
			jqtp2.populateTable(cue);
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
			jqtp2.populateTable(cue);
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
			jqtp2.populateTable(cue);
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
	 * Runs the DMFC converter, and updates 
	 * progress on the Converter Details page
	 *
	 */
	public void runScript(){	
		
		executing=true;
		this.menu.getEnableJobDetails().setEnabled(true);
		this.btnDetails.setEnabled(true);
		
		new QueueRunner(dmfc).start();
		
		UIManager.display.asyncExec(new Runnable(){
			public void run(){
				if (CurrentJobDetails.getInstance().isDisposed())return;
			
			//increment the progress bar
				CurrentJobDetails.getInstance().pb.setSelection((lel.getProgress()));
				CurrentJobDetails.getInstance().txtElapsedTime.setText(String.valueOf(lel.getTimeLeft()));
				CurrentJobDetails.getInstance().txtEstimatedTime.setText(String.valueOf(lel.getTotalTime()));
		}
	});
		
		
		//if lel.getMessage==END+SCRIPT, then disable buttons?
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
	
}


