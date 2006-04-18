package org.daisy.dmfc.gui;


import java.io.File;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.EventListener;
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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;



/**
 * First screen, and singleton for the application.
 * Command center, so to speak
 * 
 * @author Laurie Sherve
 */
public class Window extends Composite {
	private static int queueNum = 0;
	private int currentNum = 0;
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
	
	
	//Buttons
	Button addSingleFile;
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
	Table tblJobs2;
	
	//TextField
	Text txtScriptRunning1;
	
	// Queue of all Jobs
	Queue cue;
	
	//QueueManager to run scripts
	QueueRunner queRunner;
	
	//Composite
	Composite compJobsInQueue;
	
	//int 
	int index;
	
	//Array of ScriptHandler objects
	ScriptHandler [] listScriptHandlers;

	//The ScriptHandler to pass around
	ScriptHandler scriptHandler;
	
	//Selected Conversion
	private String selectedConversion;
	
	public static Window getInstance(){
		if (window==null){
			window=new Window();
		}
		return window;
	}
	
	//Properties
	
	private Window() {
		this(new Shell(UIManager.display));
	}
	
	
	private Window(final Shell shell) {
		super(shell, SWT.NONE);
		this.shell = shell;
		
		UIManager.windowNum++;
		
		new MenuDMFC(shell);
		cue=cue.getInstance();
		
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
		listProperties.setProperties(listConversion, "Conversion Name");
		populateList();
		
		listConversion.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				int selection = listConversion.getSelectionCount();
				System.out.println ("The number selected is " + selection);
				if (selection==1){
					addMultipleFiles.setEnabled(true);
					addSingleFile.setEnabled(true);
					
				}
			}
		});
		
		listConversion.addListener (SWT.DefaultSelection, new Listener () {
			public void handleEvent (Event e) {
				
				int selection = listConversion.getSelectionCount();
				System.out.println ("The number selected is " + selection);
				if (selection==1){
					addMultipleFiles.setEnabled(true);
					addSingleFile.setEnabled(true);
					
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
		
		
		this.addSingleFile = new Button (addButtonsComp, SWT.SHADOW_OUT);
		this.addSingleFile.setEnabled(false);
		buttonProperties.setProperties(addSingleFile, "Convert Single File");
		this.addSingleFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getConversionSelection();
				new ConvertSingleFile().open();
			}
		});
		
		
		this.addMultipleFiles = new Button (addButtonsComp, SWT.SHADOW_OUT);
		this.addMultipleFiles.setEnabled(false);
		
		buttonProperties.setProperties(addMultipleFiles, "Convert Multiple Files");
		this.addMultipleFiles.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getConversionSelection();
				new ConvertMultipleFiles().open();
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
		RowLayout rowLayout4 = new RowLayout(SWT.VERTICAL);
		rowLayout4.pack = true;
		rowLayout4.spacing = 15;
		compJobsInQueue.setLayout(rowLayout4);
		
		this.lblJobsInQueue2 = new Label(compJobsInQueue, SWT.NONE);
		labelProperties.setProperties(lblJobsInQueue2, "Jobs (Conversions) in Queue");
		
		this.tblJobs2 = new Table(compJobsInQueue, SWT.BORDER |SWT.V_SCROLL | SWT.H_SCROLL |SWT.SINGLE |SWT.FULL_SELECTION );
		jqtp2 = new JobQueueTableProperties(tblJobs2);
		tblJobs2.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				int mark = tblJobs2.getSelectionIndex();
				System.out.println("Index selected is " + mark);
				setSelectedIndex(mark);
			}
		});
		
		
		FormData formFill4 = new FormData();
		formFill4.top = new FormAttachment(listConversion, 35);
		formFill4.left = new FormAttachment(0, 20);
		formFill4.bottom = new FormAttachment(65, 10);
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
		buttonProperties.setProperties(btnMoveUp, "Move Up");
		this.btnMoveUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveJobUp();
				jqtp2.populateTable(cue);
			}
		});
		
		
		
		this.btnMoveDown = new Button (moveJobsComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnMoveDown, "Move Down");
		this.btnMoveDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveJobDown();
				jqtp2.populateTable(cue);
			}
		});
		
		
		this.btnDelete = new Button (moveJobsComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnDelete, "Delete");
		this.btnDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deleteJob();
				jqtp2.populateTable(cue);
			}
		});
		
		this.btnEdit = new Button (moveJobsComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnEdit, "Edit");
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
		
		
		/*button.addSelectionListener(new SelectionAdapter(){
public void widgetSelected(SelectionEvent e) {
   if (e.getSource() == DataGrid.this.button){
      TableItem[] items = DataGrid.this.table.getSelection();
      if (items == null || items.length < 1){
         Toolkit.getDefaultToolkit().beep();  
      } else {
         System.out.println("Selected key..." + items[0]);
         EditForm form = new EditForm(DataGrid.this.shell);
         form.setKey(items[0].getText());
         form.show();
         DataGrid.this.refreshData();
      }
   }  
}
});  
*/
		/*&
		table.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				String string = event.detail == SWT.CHECK ? "Checked" : "Selected";
				System.out.println (event.item + " " + string);
			}
		});
		*/
		
	//Bottom Buttons
		
		Composite bottomComp = new Composite(shell, SWT.NONE);
		
		bottomComp.setBackground(ColorChoices.white);
		RowLayout rowLayout5 = new RowLayout(SWT.HORIZONTAL);
		rowLayout5.pack = false;
		rowLayout5.spacing = 20;
		bottomComp.setLayout(rowLayout5);
		
		this.btnRun = new Button(bottomComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnRun, "Run Queue");
		this.btnRun.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				runScript();
			}
		});
		

		
		
		this.btnDetails = new Button(bottomComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnDetails, " Queue Run Details");
		this.btnDetails.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CurrentJobDetails.getInstance().open();
			}
		});
		
		
		
		this.btnTerminate = new Button(bottomComp, SWT.SHADOW_OUT);
		buttonProperties.setProperties(btnTerminate, " Terminate Run");
		
		
		FormData formFill5 = new FormData();
		formFill5.top = new FormAttachment(compJobsInQueue, 35);
		formFill5.left = new FormAttachment(0, 20);
		formFill5.bottom = new FormAttachment(66, 100);
		formFill5.right = new FormAttachment(55,10);
		bottomComp.setLayoutData(formFill5);
	    
		 shell.pack();
		
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
	
	public void addToQueue(Job job){
		
		cue.addJobToQueue(job);
		System.out.println("place in cue " + cue.getPlaceInQueue(job));		
		jqtp2.populateTable(cue);
		
	}

	
	
	/**
	 * Clear all fields in the gui
	 */
	
	public void clearFields(){
		this.clearFields();
		selectedConversion="";
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
				/*System.out.println("This is script number " + i + "  ");
				System.out.println("The description of the script is " + sh.getDescription());
				System.out.println("The name of the script is: " + sh.getName());
				System.out.println("The number of tasks in the script are: " + sh.getTaskCount());
				*/
				
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
			System.out.println("wht is the focus index? " + focus);
			
			this.scriptHandler= listScriptHandlers[focus + 1];
			
			
		}
	}
	
	public Composite getCompJobsInQueue(){
		return this.compJobsInQueue;
	}
	
	public void setSelectedIndex(int mark){
		this.index=mark;
	}
	
	public void moveJobUp(){
		if(index==cue.getSizeOfQueue()-1){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
					messageBox.setMessage("Cannot move the last item in a table higher.");
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
	
	public void moveJobDown(){
		if(index==0){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
					messageBox.setMessage("Cannot move the first item in a table lower.");
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
	
	
	public void runScript(){
		
		//java.util.List list = scriptHandler.getTransformerInfoList();	
		
		queRunner = new QueueRunner(dmfc);
		queRunner.executeJobsInQueue();
		CurrentJobDetails.getInstance().open();
				
	}
	
	public DMFCCore getDMFC(){
		return this.dmfc;
	}
	
	public LocalEventListener getLocalEventListener(){	
		return lel;
	}
	
}


