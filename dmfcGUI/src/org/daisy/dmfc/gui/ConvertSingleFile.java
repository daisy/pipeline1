package org.daisy.dmfc.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.daisy.dmfc.core.DMFCCore;
import org.daisy.dmfc.core.script.Property;
import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.gui.menus.MenuSingleConvert;
import org.daisy.dmfc.gui.widgetproperties.ButtonProperties;
import org.daisy.dmfc.gui.widgetproperties.FormAttachmentsHelper;
import org.daisy.dmfc.gui.widgetproperties.IProperties;
import org.daisy.dmfc.gui.widgetproperties.LabelProperties;
import org.daisy.dmfc.gui.widgetproperties.TextProperties;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.Status;
import org.daisy.util.mime.MIMEType;
import org.daisy.util.mime.MIMETypeException;
import org.daisy.util.mime.MIMETypeRegistry;
import org.daisy.util.mime.MIMETypeRegistryException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * User chooses a single file to convert, and output file on this screen
 * @author Laurie Sherve
 *
 */
public class ConvertSingleFile  {
	
	
	Display display;
	Shell shell;
	Window window;
	FormAttachmentsHelper fah = new FormAttachmentsHelper();
	IProperties labelProperties = new LabelProperties();
	IProperties textProperties = new TextProperties();
	IProperties buttonProperties = new ButtonProperties();
	
	
	//Labels
	Label lblNameConversion;
	Label lblConversion;
	Label lblInputDocument;
	Label lblOutputDocument;
	Label lblDaisyMFC;
	
	//Buttons
	public Button btnBrowseInput;
	public Button btnBrowseOutput;
	public Button btnOK;
	public Button btnCancel;
	
	//TextFields
	Text txtConversionName;
	Text txtInputDoc;
	Text txtOutputDoc;
	
	//String
	String fileSelected;
	String outputPath;
	String script;
	String outExtensionPattern;
	
	
	//boolean - is this an edit or a single file selection?
	//defaults to editFile, false.
	boolean editFile = false;
	boolean boolOutputIsDir = false;
	
	//Array of output mime types
	String [] arFileOrDir = null;
	
	//ArrayList of Jobs
	ArrayList alJobs = new ArrayList();
	
	//Original job.  If used in edit mode, this is returned if canceled
	Job editJob;
	
	//ScriptHandler object, get name of the conversion
	ScriptHandler scriptHandler;
	
	//GridData
	GridData data;
	
	private DMFCCore dmfc;
	
	
	public ConvertSingleFile(){
		display= UIManager.display;
		shell = new Shell(display, SWT.APPLICATION_MODAL |SWT.SHELL_TRIM);
		new MenuSingleConvert(shell);
		createContents();
		shell.pack();
	}
		
	
	public void createContents(){
		
		shell.setText("Add Single Source");
		//shell.setBounds(0,0,500,500);
		shell.setLocation(250, 250);
		
		GridLayout layout = new GridLayout();
		layout.numColumns=1;
		layout.marginTop=0;
		layout.marginBottom=10;
		layout.marginRight=10;
		layout.marginLeft=15;
		shell.setLayout(layout);
		
		//3 composites with borders for accessibility
		
		//Composite conversion stuff	
		Group compConversionChosen = new Group(shell, SWT.NONE);
		//compConversionChosen.setBackground(ColorChoices.white);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compConversionChosen.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compConversionChosen.setLayout(layout);
		
		
		
//		Label
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		lblNameConversion = new Label(compConversionChosen, SWT.NONE);
		lblNameConversion.setText("Name of Converter");
		lblNameConversion.setLayoutData(data);
		

//		Label with Conversion Chosen
		
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		data.widthHint=250;
		lblConversion = new Label(compConversionChosen, SWT.BORDER);
		lblConversion.setLayoutData(data);
		scriptHandler = window.getInstance().getConversionChosen();
		lblConversion.setText(scriptHandler.getName());
		
		//End ScriptHandler stuff

		
		//Input stuff
		Group compInputFields = new Group(shell, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compInputFields.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compInputFields.setLayout(layout);
		
		
//		Label for input Doc
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		lblInputDocument = new Label(compInputFields, SWT.NONE);
		labelProperties.setProperties(lblInputDocument, "Source File");
		lblInputDocument.setLayoutData(data);
		
//		Text Field, input choice
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.widthHint=300;
		txtInputDoc = new Text(compInputFields, SWT.BORDER);
		textProperties.setProperties(txtInputDoc, null);
		txtInputDoc.setLayoutData(data);
		
//		Browse button
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		btnBrowseInput= new Button(compInputFields, SWT.BORDER);
		buttonProperties.setProperties(btnBrowseInput, "Browse");
		btnBrowseInput.setLayoutData(data);
		this.btnBrowseInput.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setFileSelected();
			}
		});
		
		
		
		//Composite output
		
		Group compOutputFields = new Group(shell, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		compOutputFields.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=8;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compOutputFields.setLayout(layout);
		
//		Label for Output Doc
		lblOutputDocument = new Label(compOutputFields, SWT.NONE);
		lblOutputDocument.setText("Destination");
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan=2;
		lblOutputDocument.setLayoutData(data);
		
		//Text field
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.widthHint=300;
		txtOutputDoc=new Text(compOutputFields, SWT.BORDER);
		textProperties.setProperties(txtOutputDoc, null);
		txtOutputDoc.setLayoutData(data);
		
		
		//Browse Button
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		btnBrowseOutput = new Button(compOutputFields, SWT.BORDER);
		buttonProperties.setProperties(btnBrowseOutput, "Browse");
		btnBrowseInput.setLayoutData(data);
		this.btnBrowseOutput.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setOutputPathSelected();
			}
		});
		
		
		//bottom OK and Cancel buttons
		Composite compOkCancelButtons = new Composite (shell, SWT.NONE);
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER );
		compOkCancelButtons.setLayoutData(data);
		layout = new GridLayout();
		layout.horizontalSpacing=12;
		layout.numColumns=2;
		layout.marginTop=0;
		layout.marginBottom=5;
		layout.marginWidth=7;
		compOkCancelButtons.setLayout(layout);
		
		data=new GridData(GridData.HORIZONTAL_ALIGN_CENTER );
		btnOK = new Button(compOkCancelButtons, SWT.BORDER);
		buttonProperties.setProperties(btnOK, "OK");
		btnOK.setLayoutData(data);
		
		btnOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//if (lblDaisyMFC.getText().equalsIgnoreCase("Edit Conversion")){
				if (editFile == true){
					sendEditedJobToMain();
				}
				sendJobInfoToMain();
			}
		});
		
		data=new GridData(GridData.HORIZONTAL_ALIGN_CENTER );
		btnCancel= new Button(compOkCancelButtons, SWT.BORDER);
		buttonProperties.setProperties(btnCancel, "Cancel");
		btnCancel.setLayoutData(data);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//if (lblDaisyMFC.getText().equalsIgnoreCase("Edit Conversion")){
				if (editFile == true){
					Window.getInstance().addToQueue(editJob);
				}
				dispose();
			}
		});
		
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
	
	
//	Methods called by Listeners
	/**
	 * 
	 */
	public void setFileSelected() {
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setText("Choose an input file");
		if (txtInputDoc != null) {
			File file = new File(txtInputDoc.getText());
			if (file.exists() && !file.isDirectory()) {
				file = file.getParentFile();
			}
			//dlg.setFilterPath(file.getAbsolutePath());
			dlg.setFilterPath(FilePaths.singleInputPath);
		}
		
		//filter names shown
		dlg.setFilterExtensions(this.getGlobFromMime(this.getMimeForProperty("input")));
		
		fileSelected = dlg.open();		
		
		if (fileSelected!=null ){
			//System.out.println("File selected  " + fileSelected);
			//System.out.println("The filter path is: " + dlg.getFilterPath());
			FilePaths.singleInputPath=dlg.getFilterPath();
			this.txtInputDoc.setText(fileSelected);
		}
		
	}
	
	public void setOutputPathSelected() {
		
		String mimeOut = this.getMimeForProperty("outputPath");
		if ("application/x-filesystemDirectory".equals(mimeOut)) {	
			// Directory
			DirectoryDialog directoryDialog = new DirectoryDialog(shell);
			directoryDialog.setText("Choose output directory");
			directoryDialog.setFilterPath(FilePaths.singleOutputPath);
			outputPath = directoryDialog.open();
			System.out.println("outputPath Selected  " + outputPath);
			this.txtOutputDoc.setText("");
			if (outputPath==null ){
				//System.out.println("outputPath is not selected " );
			}
			else{
				//add a new directory to this...
				this.txtOutputDoc.setText(outputPath);
				FilePaths.singleOutputPath=directoryDialog.getFilterPath();
				//System.out.println("outpath path is " + outputPath);
			}
		} else {
			// File
			FileDialog dlg = new FileDialog(shell, SWT.SAVE);
			dlg.setText("Choose output file");
			if (txtOutputDoc != null) {
				File file = new File(txtOutputDoc.getText());
				if (file.exists() && !file.isDirectory()) {
					file = file.getParentFile();
				}
				//dlg.setFilterPath(file.getAbsolutePath());
				dlg.setFilterPath(FilePaths.singleOutputPath);
			}
			dlg.setFilterExtensions(this.getGlobFromMime(mimeOut));			
			outputPath = dlg.open();					
			if (outputPath!=null ){
				this.txtOutputDoc.setText(outputPath);
				FilePaths.singleOutputPath=dlg.getFilterPath();
			}
		}
	}
	
	public void sendJobInfoToMain() {
		if (txtInputDoc.getText().equalsIgnoreCase("") || txtInputDoc == null
				|| txtOutputDoc.getText().equalsIgnoreCase("")
				|| txtOutputDoc == null) {
			
			// display an error message and return.
			
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR |
					SWT.CANCEL);
			messageBox.setMessage("File to Convert and Output File must be completed");
			messageBox.setText("Error:  Complete Fields");
			messageBox.open();
			
			
		} else {			
			Job job = new Job();
			job.setInputFile(new File(fileSelected));
			job.setOutputFile(new File(outputPath));
			job.setScript(scriptHandler);
			job.setStatus(Status.WAITING);
			Window.getInstance().addToQueue(job);
			
			dispose();
		}
	}
	
	
	/**
	 * Sets information to be edited.
	 * @param Job job, called from Job table at Window.
	 */
	public void editConversion(Job job){
		this.editJob=job;
		editFile= true;
		shell.setText("Daisy Multi-Format Converter, Edit Conversion");
		//txtConversionName.setText(job.getScript().getName());
		lblConversion.setText(job.getScript().getName());
		txtInputDoc.setText(job.getInputFile().getPath());
		txtOutputDoc.setText(job.getOutputFile().getPath());
		scriptHandler = job.getScript();
		
	}
	
	public void sendEditedJobToMain(){
		this.fileSelected= txtInputDoc.getText();
		this.outputPath=txtOutputDoc.getText();
	}
	
	
	
	public String[] getGlobFromMime(String mime) {
		if (mime == null) {
			return null;
		}
		try {
			MIMEType type = MIMETypeRegistry.getInstance().getEntryByName(mime);
			if (type != null) {
				Object[] arr = type.getFilenamePatterns().toArray();
				String[] ret = new String[arr.length + 1];
				for (int i = 0; i < arr.length; ++i) {
					ret[i] = (String)arr[i];
				}
				ret[arr.length] = "*.*";
				System.err.println("Glob: " + ret);
				return ret;
			} 		
		} catch (MIMETypeRegistryException e) {
		} catch (MIMETypeException e) {
		}
		return null;
	}
	
	public String getMimeForProperty(String propertyName) {
		ScriptHandler handler = this.scriptHandler;
		Map properties = handler.getProperties();
		Property prop = (Property)properties.get(propertyName);
		try{
			if (prop.getType() != null && !prop.getType().equals("")) {
				System.out.println("The prop type is " + prop.getType());
				return prop.getType();
			}
			else{
				System.out.println("Property type equals \"\" ");
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage() + "Property type is null");
		}
		return null;		
	}
	
	
}
