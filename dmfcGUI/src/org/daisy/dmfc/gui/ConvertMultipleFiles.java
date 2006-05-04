package org.daisy.dmfc.gui;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.core.transformer.ParameterInfo;
import org.daisy.dmfc.core.transformer.TransformerInfo;
import org.daisy.dmfc.gui.menus.MenuMultipleConvert;
import org.daisy.dmfc.gui.widgetproperties.ButtonProperties;
import org.daisy.dmfc.gui.widgetproperties.ColorChoices;
import org.daisy.dmfc.gui.widgetproperties.CompatibleFilesTableProperties;
import org.daisy.dmfc.gui.widgetproperties.FontChoices;
import org.daisy.dmfc.gui.widgetproperties.FormAttachmentsHelper;
import org.daisy.dmfc.gui.widgetproperties.IProperties;
import org.daisy.dmfc.gui.widgetproperties.LabelProperties;
import org.daisy.dmfc.gui.widgetproperties.RadioButtonProperties;
import org.daisy.dmfc.gui.widgetproperties.TextProperties;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.viewers.*;


/**
 * Used to select multiple files.
 * All files selected will use the same conversion as listed 
 * on the top of the screen.
 * @author Laurie Sherve
 *
 */
public class ConvertMultipleFiles {

	//for singleton
	private static ConvertMultipleFiles instance;
	
	Shell shell;
	Display display;
	Window window;
	FormAttachmentsHelper fah = new FormAttachmentsHelper();
	CompatibleFilesTableProperties cftp;
	IProperties labelProperties = new LabelProperties();
	IProperties textProperties = new TextProperties();
	IProperties buttonProperties = new ButtonProperties();
	IProperties radioButtonProperties = new RadioButtonProperties();

	// Labels
	Label lblNameConversion;
	Label lblInputDocument;
	Label lblOutputDocument;
	Label lblOnlyCompatibleShown;

	// Buttons
	Button btnBrowseInput;
	Button btnBrowseOutput;
	Button btnOK;
	Button btnCancel;
	Button btnRadio1;
	Button btnRadio2;

	// TextFields
	Text txtConversionName;
	Text txtDirectorySelected;
	Text txtInputDoc;
	Text txtOutputDoc;
	
	//Tables
    Table tblCompatibleFiles;
	
	// String
	String dirSelected ;
	String outputPath = "";
	String script = "";
	String strSubfolderOfInputFolder;
	String strSubfolderOfOutputFolder;

	//ScriptHandler
	ScriptHandler scriptHandler;
	
	//ArrayList to hold selected input files
	ArrayList al = new ArrayList();
	
	
	public ConvertMultipleFiles(){
		display= UIManager.display;
		shell = new Shell(display);
		new MenuMultipleConvert(shell);
		createContents();
		shell.pack();
	}
	
	
/*	public ConvertMultipleFiles() {
		this(new Shell(UIManager.display));
	}
*/
	/**
	 * Constructor
	 * @param shell
	 */
/*	public ConvertMultipleFiles(final Shell shell) {
			super(shell, SWT.NONE);
			this.shell = shell;
			*/
			
		public void createContents(){	
			
			TreeViewer viewer = null;
			
			shell.setText("Daisy Multi Format Converter");
			shell.setMaximized(true);
			shell.setBackground(ColorChoices.white);
			
			// shell.setSize(356, 275);
			// shell.setLocation(214, 216);
			shell.setLayout(new FormLayout());
			
			
			// Title
			Label lblDaisyMFC = new Label(shell, SWT.NONE);
			lblDaisyMFC.setForeground(ColorChoices.darkBlue);
			lblDaisyMFC.setText("Select Files to be Converted");
			lblDaisyMFC.setFont(FontChoices.fontSubTitle);
			lblDaisyMFC.setBackground(ColorChoices.white);
			
			FormData formData = new FormData();
			fah.setFormData(formData, 0,20,30,20,8,10,65,10);
			lblDaisyMFC.setLayoutData(formData);
			
			
		//Composite top	
			Composite compConversionChosen = new Composite(shell, SWT.NONE);
			compConversionChosen.setBackground(ColorChoices.white);
			RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
			rowLayout.spacing = 20;
			rowLayout.wrap = true;
			rowLayout.fill = false;
			rowLayout.justify = false;
			compConversionChosen.setLayout(rowLayout);
			
//			Label
			lblNameConversion = new Label(compConversionChosen, SWT.NONE);
			labelProperties.setProperties(lblNameConversion, "Name of Conversion Selected");
			RowData dataLabel = new RowData();
			dataLabel.width = 250;
		 	lblNameConversion.setLayoutData(dataLabel);
			
//			Text area
			txtConversionName = new Text(compConversionChosen, SWT.BORDER);
			textProperties.setProperties(txtConversionName, null);	
			RowData dataText = new RowData();
			dataText.width = 250;
		 	txtConversionName.setLayoutData(dataText);
		 	scriptHandler = window.getInstance().getConversionChosen();
		 	txtConversionName.setText(scriptHandler.getName());
		
//			
			FormData formDatalblCon = new FormData();
			 fah.setFormData(formDatalblCon, 10,10,17,10,17,10,75,10);
			 compConversionChosen.setLayoutData(formDatalblCon);
		//End Conversion stuff
			 
				
		// Composite Input stuff
			 Composite compInputFields = new Composite(shell, SWT.NONE);
			 compInputFields.setBackground(ColorChoices.white);
			 GridLayout gridLayout = new GridLayout(3, false);
			 compInputFields.setLayout(gridLayout);
			
			  // Label folder to search in
			 lblInputDocument = new Label(compInputFields, SWT.NONE);
			 labelProperties.setProperties(lblInputDocument, "Select Folder");
			 lblInputDocument.pack();
			 GridData data = new GridData(SWT.LEFT | SWT.CENTER );
			 data.horizontalSpan=1;
			 lblInputDocument.setLayoutData(data);
			 
			 
			 // TextField to hold folder chosen
			 txtDirectorySelected = new Text(compInputFields, SWT.BORDER);
			 textProperties.setProperties(txtDirectorySelected, "");
			 data = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
			 data.horizontalSpan=1;
			 data.widthHint = 300;
			 txtDirectorySelected.setLayoutData(data);
			 
			 
           // Browse button
			 btnBrowseInput= new Button(compInputFields, SWT.BORDER);
			 data = new GridData();
			 data.horizontalSpan=1;
			 btnBrowseInput.setLayoutData(data);
			 buttonProperties.setProperties(btnBrowseInput, "Browse");
			 
			 this.btnBrowseInput.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						setDirectorySelected();
						populateCompatibleFilesTable();
					}
				});
			 
			 
			 
			 lblOnlyCompatibleShown = new Label(compInputFields, SWT.NONE);
			 data = new GridData();
			 data.horizontalSpan = 3;
			 lblOnlyCompatibleShown.setLayoutData(data);
			 labelProperties.setProperties(lblOnlyCompatibleShown, "(Only Compatible File Types Displayed)");
			
			 data = new GridData();
			 data.horizontalSpan=3;
			 data.heightHint=140;
			 tblCompatibleFiles= new Table(compInputFields, SWT.CHECK |SWT.BORDER |SWT.V_SCROLL |SWT.MULTI );
			 tblCompatibleFiles.setLayoutData(data);
			 cftp = new CompatibleFilesTableProperties(tblCompatibleFiles);
			
			 
			 tblCompatibleFiles.addListener (SWT.Selection, new Listener () {
					public void handleEvent (Event event) {
						//String string = event.detail == SWT.CHECK ? "Checked" : "Selected";
						//adds a TableItem to the arrayList
						al.add(event.item);
					}
				});
			 
			
			 
		
			 FormData formDataDocInput = new FormData();
			 formDataDocInput.top = new FormAttachment(compConversionChosen, 10);
			 formDataDocInput.left = new FormAttachment(17, 10);
			 formDataDocInput.bottom = new FormAttachment(60, 10);
			 //formDataDocInput.bottom = new FormAttachment(70, 10);
			 formDataDocInput.right = new FormAttachment(75,10);
			 compInputFields.setLayoutData(formDataDocInput);
			 
		//end input stuff
				
			 
			 // Label for outputdoc
// Label for Output Doc
			 lblOutputDocument = new Label(shell, SWT.NONE);
			 labelProperties.setProperties(lblOutputDocument, "Output Path");
			 
			 FormData formDataLabelOut = new FormData();
			 formDataLabelOut.top = new FormAttachment(compInputFields, 10);
			 formDataLabelOut.left = new FormAttachment(17, 10);
			 formDataLabelOut.bottom = new FormAttachment(64, 10);
			 formDataLabelOut.right = new FormAttachment(75,10);
			 lblOutputDocument.setLayoutData(formDataLabelOut);
			 
			 
	// Composite output
			 
			 Composite compOutputFields = new Composite(shell, SWT.NONE);
			 compOutputFields.setBackground(ColorChoices.white);
			 
			 GridLayout gridLay = new GridLayout();
			 gridLay.numColumns = 2;
			 compOutputFields.setLayout(gridLay);
			 
         
			
		// RadioButtons
			 Composite compRadioButtons = new Composite(compOutputFields, SWT.BORDER);
			 compRadioButtons.setBackground(ColorChoices.white);
			 RowLayout rowLayoutRadio = new RowLayout(SWT.VERTICAL);
			 rowLayout.spacing = 20;
			 rowLayout.wrap = true;
			 rowLayout.fill = false;
			 rowLayout.justify = false;
			 compRadioButtons.setLayout(rowLayoutRadio);
			 
			 btnRadio1= new Button(compRadioButtons, SWT.RADIO);
			 radioButtonProperties.setProperties(btnRadio1, "Subfolder of Input Document");
			 btnRadio1.setSelection(true);
			 this.btnRadio1.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						btnBrowseOutput.setEnabled(false);
						setOutputPath();
					}
				});
			 
			 
			 btnRadio2= new Button(compRadioButtons, SWT.RADIO);
			 radioButtonProperties.setProperties(btnRadio2, "Subfolder of Selected Folder");
			 this.btnRadio2.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						btnBrowseOutput.setEnabled(true);
						resetOutputPath();
					}
				});
			 
			 GridData dataRadio = new GridData();
			 dataRadio.horizontalSpan = 2;
			 compRadioButtons.setLayoutData(dataRadio);
		
			 //Text field
			 txtOutputDoc=new Text(compOutputFields, SWT.BORDER);
			 textProperties.setProperties(txtOutputDoc, null);
			 GridData dataInput = new GridData(GridData.FILL_HORIZONTAL);
			 txtOutputDoc.setLayoutData(dataInput);
			 
			 
          //Browse Button
			 btnBrowseOutput = new Button(compOutputFields, SWT.BORDER);
			 btnBrowseOutput.setEnabled(false);
			 buttonProperties.setProperties(btnBrowseOutput, "Browse");
			 GridData dataBrowse = new GridData();
			 btnBrowseOutput.setLayoutData(dataBrowse); 
			 this.btnBrowseOutput.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						setOutputPathSelected();
						
						//getFileTypesForScriptHandler();
					}
				});
			 
			 
			 FormData formDataOutput = new FormData();
			 formDataOutput.top = new FormAttachment(lblOutputDocument, 10);
			 formDataOutput.left = new FormAttachment(17, 10);
			 formDataOutput.bottom = new FormAttachment(79, 10);
			 formDataOutput.right = new FormAttachment(75,10);
			 compOutputFields.setLayoutData(formDataOutput);
			 
	// bottom OK and Cancel buttons
			 Composite compOkCancelButtons = new Composite (shell, SWT.NONE);
			 compOkCancelButtons.setBackground(ColorChoices.white);
			 RowLayout rowLayout3 = new RowLayout(SWT.HORIZONTAL | SWT.RIGHT);
			 rowLayout3.pack = false;
			 rowLayout3.spacing = 25;
			 compOkCancelButtons.setLayout(rowLayout3); 
			 
			 btnOK = new Button(compOkCancelButtons, SWT.BORDER);
			 buttonProperties.setProperties(btnOK, "OK");
			 btnOK.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						sendJobInfoToMain();
						
					}
				});
			 
			 btnCancel= new Button(compOkCancelButtons, SWT.BORDER);
			 buttonProperties.setProperties(btnCancel, "Cancel");
			 btnCancel.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						dispose();
					}
				});
			 
			 FormData formDataOkCancel = new FormData();
			 formDataOkCancel.top = new FormAttachment(compOutputFields, 10);
			 formDataOkCancel.left = new FormAttachment(38, 10);
			 formDataOkCancel.bottom = new FormAttachment(88, 10);
			 formDataOkCancel.right = new FormAttachment(75,10);
			 compOkCancelButtons.setLayoutData(formDataOkCancel);
			 
			 instance=this;
			 shell.pack();
		}	

	
	public ConvertMultipleFiles getInstance(){
		return instance;
	}
	
	public void open() {
		shell.open();

		while (!shell.isDisposed())
			if (!UIManager.display.readAndDispatch())
				UIManager.display.sleep();
	}

	public void dispose() {
		shell.dispose();
	}
	
	//calls from listeners
	
	public void populateCompatibleFilesTable(){
		if (dirSelected==null){
			System.out.println("Directory selected is null");
		}
		else
			cftp.populateTable(new File(dirSelected));	
	}
	
	
	public void sendJobInfoToMain() {
		if (tblCompatibleFiles.getItemCount()==0 
				|| txtOutputDoc.getText().equalsIgnoreCase("")
				|| txtOutputDoc == null) {

			// display an error message and return.
			
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
					messageBox.setMessage("Compatible Files Table and Output paths must be completed");
					messageBox.setText("Error:  Complete Fields");
					messageBox.open();			
		}
		
		else if(al.isEmpty()){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
					messageBox.setMessage("Please check files to be converted.");
					messageBox.setText("Error:  Choose Compatible Files");
					messageBox.open();
		}
	
		else {
			//all use the same conversion
			//Create all job objects from each input file chosen
			
			outputPath = txtOutputDoc.getText();
			
			Iterator it = al.iterator();
			while (it.hasNext()){
				Job job = new Job();
				TableItem ti = (TableItem) it.next();
				job.setInputFile(new File (ti.getText()));
				job.setOutputFile(new File(outputPath));
				job.setScript(scriptHandler);
				job.setStatus(Status.WAITING);
				Window.getInstance().addToQueue(job);
			
			}
			al.clear();
			dispose();
		}
	}
	
	
	
	/**
	 * 
	 *
	 */
	public void setDirectorySelected() {
		
		DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		directoryDialog.setText("Choose a directory");
		directoryDialog.setFilterPath("/");
		dirSelected = directoryDialog.open();
		if (dirSelected==null){
			System.out.println("dirSelected is Null");
		}
		else{
			System.out.println("Directory Selected  " + dirSelected);
			txtDirectorySelected.setText(dirSelected);
			
			//set the output path to a subfolder of the input directory selected
			
			String lastDir = "";
			StringTokenizer st = new StringTokenizer(dirSelected, File.separator);
			while(st.hasMoreTokens()){
				lastDir = st.nextToken();
				
			}
			System.out.println("The last token is " + lastDir);
			strSubfolderOfInputFolder =dirSelected + File.separator + lastDir + File.separator;
			txtOutputDoc.setText(strSubfolderOfInputFolder);
			//txtOutputDoc.setText(dirSelected + File.separator + lastDir + File.separator);
			
			
		}
		
	}
	
	
	public void setOutputPath(){
		if (strSubfolderOfInputFolder==null){
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
					messageBox.setMessage("Sorry, First select an input folder.");
					messageBox.setText("Error:  Select Input Folder");
					messageBox.open();	
		}
		else{
			txtOutputDoc.setText(strSubfolderOfInputFolder);
		}
	}
	
	
	public void resetOutputPath(){
		txtOutputDoc.setText("");
	}
	
	
	public void setOutputPathSelected() {
		if (btnRadio2.isEnabled()){
			DirectoryDialog directoryDialog = new DirectoryDialog(shell);
			directoryDialog.setText("Choose a directory");
			directoryDialog.setFilterPath("/");
			outputPath = directoryDialog.open();
			
			if (outputPath!=null){
				
				String lastDir = "";
				StringTokenizer st = new StringTokenizer(outputPath, File.separator);
				while(st.hasMoreTokens()){
					lastDir = st.nextToken();
					
				}
				System.out.println("The last token is " + lastDir);
				strSubfolderOfOutputFolder =outputPath + File.separator + lastDir + File.separator;
				txtOutputDoc.setText(strSubfolderOfOutputFolder);
				
				
			}
		}
		
		else{
			//do nothing
		}
		
	}
	
	public void getFileTypesForScriptHandler(){
		
		System.out.println("GetFileTypes for Script");
		
		String fileType = null;
		
		List list= this.scriptHandler.getTransformerInfoList();
	
		 // get info on first transformer, change to list.get(list.size() - 1) for the last transformer
		TransformerInfo tinfo = (TransformerInfo)list.get(0);
		
		//Returns a collection of parameter information
		Collection col = tinfo.getParameters();
		
		Iterator it = col.iterator();		
		
		while(it.hasNext()){
			ParameterInfo pi =(ParameterInfo)it.next();
			String parameter = pi.getDirection();
			if (parameter.equalsIgnoreCase("in")){
				fileType = pi.getType();
				System.out.println("Valid types for this script " + fileType);
			}
		}	
			
		}
	//}
	
	
	
	/**
	 * "Another problem would be that a transformer can have multiple
	 *  input and output parameters, so it might not be possible to 
	 *  have a single input file type and output file type for a script."
	 *  email from Linus
	 *
	 */
	public void filterDirectorySelected(){
		
	}
	
	public Table getTableCompatibleFiles(){
		return this.tblCompatibleFiles;
	}
	
	//public Table
	
}
