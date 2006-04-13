package org.daisy.dmfc.gui;

import java.io.File;
import java.util.ArrayList;

import org.daisy.dmfc.core.script.ScriptHandler;
import org.daisy.dmfc.gui.menus.MenuSingleConvert;
import org.daisy.dmfc.gui.widgetproperties.ButtonProperties;
import org.daisy.dmfc.gui.widgetproperties.ColorChoices;
import org.daisy.dmfc.gui.widgetproperties.FontChoices;
import org.daisy.dmfc.gui.widgetproperties.FormAttachmentsHelper;
import org.daisy.dmfc.gui.widgetproperties.IProperties;
import org.daisy.dmfc.gui.widgetproperties.LabelProperties;
import org.daisy.dmfc.gui.widgetproperties.TextProperties;
import org.daisy.dmfc.qmanager.Job;
import org.daisy.dmfc.qmanager.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * User chooses a single file, and output file to convert on this screen
 * @author Laurie Sherve
 *
 */
public class ConvertSingleFile extends Composite {

	Shell shell;
	Window window;
	FormAttachmentsHelper fah = new FormAttachmentsHelper();
	IProperties labelProperties = new LabelProperties();
	IProperties textProperties = new TextProperties();
	IProperties buttonProperties = new ButtonProperties();
	
	
	//Labels
	Label lblNameConversion;
	Label lblInputDocument;
	Label lblOutputDocument;
	
	//Buttons
	Button btnBrowseInput;
	Button btnBrowseOutput;
	Button btnOK;
	Button btnCancel;
	
	//TextFields
	Text txtConversionName;
	Text txtInputDoc;
	final Text txtOutputDoc;
	
	//String
	String fileSelected;
	String outputPath;
	String script;
	
	//ArrayList of Jobs
	ArrayList alJobs = new ArrayList();
	
	
	//ScriptHandler object, get name of the conversion
	ScriptHandler scriptHandler;
	
	public ConvertSingleFile() {
		this(new Shell(UIManager.display));
	}
	
	
	public ConvertSingleFile(final Shell shell) {
		super(shell, SWT.NONE);
		this.shell = shell;
		
		UIManager.windowNum++;
		new MenuSingleConvert(shell);
		
		
		shell.setText("Daisy Multi Format Converter");
		shell.setMaximized(true);
		shell.setBackground(ColorChoices.white);
		
		
		shell.setLayout(new FormLayout());
		
		
		//Title
		Label lblDaisyMFC = new Label(shell, SWT.NONE);
		lblDaisyMFC.setForeground(ColorChoices.darkBlue);
		lblDaisyMFC.setText("Convert Single File");
		lblDaisyMFC.setFont(FontChoices.fontSubTitle);
		lblDaisyMFC.setBackground(ColorChoices.white);
		
		FormData formData = new FormData();
		fah.setFormData(formData, 0,20,35,20,8,10,75,10);
		lblDaisyMFC.setLayoutData(formData);
		
	//Composite conversion stuff	
		Composite compConversionChosen = new Composite(shell, SWT.NONE);
		compConversionChosen.setBackground(ColorChoices.white);
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.spacing = 20;
		rowLayout.wrap = true;
		rowLayout.fill = false;
		rowLayout.justify = false;
		compConversionChosen.setLayout(rowLayout);
		
//		Label
		lblNameConversion = new Label(compConversionChosen, SWT.NONE);
		labelProperties.setProperties(lblNameConversion, "Name of Conversion Selected");
		RowData dataLabel = new RowData();
		dataLabel.width = 250;
	 	lblNameConversion.setLayoutData(dataLabel);
		
//		Text area
		txtConversionName = new Text(compConversionChosen, SWT.BORDER);
		textProperties.setProperties(txtConversionName, null);	
		RowData dataText = new RowData();
		dataText.width = 250;
	 	txtConversionName.setLayoutData(dataText);
	 	scriptHandler = window.getInstance().getConversionChosen();
	 	txtConversionName.setText(scriptHandler.getName());
	 	//script=txtConversionName.getText();
//		
		FormData formDatalblCon = new FormData();
		 fah.setFormData(formDatalblCon, 10,10,17,10,17,10,75,10);
		 compConversionChosen.setLayoutData(formDatalblCon);
	//End Conversion stuff
		 
		 
	//	Label for input Doc
		 lblInputDocument = new Label(shell, SWT.NONE);
		 labelProperties.setProperties(lblInputDocument, "File to Convert");
		 
		 FormData formDataDocInput = new FormData();
		 formDataDocInput.top = new FormAttachment(compConversionChosen, 35);
		 formDataDocInput.left = new FormAttachment(17, 10);
		 formDataDocInput.bottom = new FormAttachment(25, 10);
		 formDataDocInput.right = new FormAttachment(75,10);
		 lblInputDocument.setLayoutData(formDataDocInput);
		//label for input doc 
		 
		
	//Input stuff
		 Composite compInputFields = new Composite(shell, SWT.NONE);
		 compInputFields.setBackground(ColorChoices.white);
		 RowLayout rowLayout1 = new RowLayout(SWT.HORIZONTAL);
		 rowLayout.spacing = 30;
		 rowLayout.wrap = true;
		 rowLayout.fill = false;
		 rowLayout.justify = false;
		 compInputFields.setLayout(rowLayout1);
			
//		 Text Field, input choice
		 txtInputDoc = new Text(compInputFields, SWT.BORDER);
		 textProperties.setProperties(txtInputDoc, null);
		 dataText = new RowData();
			dataText.width = 450;
			txtInputDoc.setLayoutData(dataText);
		 
//		 Browse button
		 btnBrowseInput= new Button(compInputFields, SWT.BORDER);
		 buttonProperties.setProperties(btnBrowseInput, "Browse");
		 RowData data = new RowData();
		 data.width = 60;
		 btnBrowseInput.setLayoutData(data);
		 this.btnBrowseInput.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setFileSelected();
				}
			});
		 
		 FormData formDataInput = new FormData();
		 formDataInput.top = new FormAttachment(lblInputDocument, 5);
		 formDataInput.left = new FormAttachment(17, 10);
		 formDataInput.bottom = new FormAttachment(32, 10);
		 formDataInput.right = new FormAttachment(75,10);
		 compInputFields.setLayoutData(formDataInput);
		 //end input stuff
		

//		Label for Output Doc
		 lblOutputDocument = new Label(shell, SWT.NONE);
		 labelProperties.setProperties(lblOutputDocument, "Output File");
		 
		 FormData formDataLabelOut = new FormData();
		 formDataLabelOut.top = new FormAttachment(compInputFields, 30);
		 formDataLabelOut.left = new FormAttachment(17, 10);
		 formDataLabelOut.bottom = new FormAttachment(40, 10);
		 formDataLabelOut.right = new FormAttachment(75,10);
		 lblOutputDocument.setLayoutData(formDataLabelOut);
		 
		 
		 //Composite output
		 
		 Composite compOutputFields = new Composite(shell, SWT.NONE);
		 compOutputFields.setBackground(ColorChoices.white);
		 RowLayout rowLayout2 = new RowLayout(SWT.HORIZONTAL);
		 rowLayout.spacing = 30;
		 rowLayout.wrap = true;
		 rowLayout.fill = false;
		 rowLayout.justify = false;
		 compOutputFields.setLayout(rowLayout2);
		
		 //Text field
		 txtOutputDoc=new Text(compOutputFields, SWT.BORDER);
		 textProperties.setProperties(txtOutputDoc, null);
		 dataText = new RowData();
		 dataText.width = 450;
		 txtOutputDoc.setLayoutData(dataText);
		 /*
		 txtOutputDoc.addVerifyListener(new VerifyListener() {
					public void handleEvent (Event e) {
						String string = e.txtOutputDoc;
						
						
					}
				});
		*/		
		 
		 //Browse Button
		 btnBrowseOutput = new Button(compOutputFields, SWT.BORDER);
		 buttonProperties.setProperties(btnBrowseOutput, "Browse");
		 data = new RowData();
		 data.width = 60;
		 btnBrowseInput.setLayoutData(data);
		 this.btnBrowseOutput.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setOutputPathSelected();
				}
			});
		 
		 
		 FormData formDataOutput = new FormData();
		 formDataOutput.top = new FormAttachment(lblOutputDocument, 5);
		 formDataOutput.left = new FormAttachment(17, 10);
		 formDataOutput.bottom = new FormAttachment(48, 10);
		 formDataOutput.right = new FormAttachment(75,10);
		 compOutputFields.setLayoutData(formDataOutput);
		 
		 //bottom OK and Cancel buttons
		 Composite compOkCancelButtons = new Composite (shell, SWT.NONE);
		 compOkCancelButtons.setBackground(ColorChoices.white);
		 RowLayout rowLayout3 = new RowLayout(SWT.HORIZONTAL);
		 rowLayout.spacing = 40;
		 rowLayout.wrap = true;
		 rowLayout.fill = false;
		 rowLayout.justify = false;
		 compOkCancelButtons.setLayout(rowLayout3); 
		 
		 btnOK = new Button(compOkCancelButtons, SWT.BORDER);
		 buttonProperties.setProperties(btnOK, "OK");
		 data = new RowData();
		 data.width = 60;
		 btnOK.setLayoutData(data);
		 btnOK.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					sendJobInfoToMain();
					
				}
			});
		
		 
		 btnCancel= new Button(compOkCancelButtons, SWT.BORDER);
		 buttonProperties.setProperties(btnCancel, "Cancel");
		 data = new RowData();
		 data.width = 60;
		 btnCancel.setLayoutData(data);
		 btnCancel.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					dispose();
				}
			});
		 
		 FormData formDataOkCancel = new FormData();
		 formDataOkCancel.top = new FormAttachment(compOutputFields, 35);
		 formDataOkCancel.left = new FormAttachment(38, 10);
		 formDataOkCancel.bottom = new FormAttachment(60, 10);
		 formDataOkCancel.right = new FormAttachment(75,10);
		 compOkCancelButtons.setLayoutData(formDataOkCancel);
		 
		 
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
	
	
//Methods called by Listeners
	/**
	 * @todo problem, text field contains something
	 */
	public void setFileSelected() {
		FileDialog fileDialog = new FileDialog(shell);
		fileDialog.setText("Choose an input file");
		fileDialog.setFilterPath("c://");
		fileSelected = fileDialog.open();
		
		/*
		DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		directoryDialog.setText("Choose a directory");
		directoryDialog.setFilterPath("/");
		fileSelected = directoryDialog.open();
		*/
		
		
		this.txtInputDoc.setText("");
		if (fileSelected!=null ){
			System.out.println("Directory Selected  " + fileSelected);
			this.txtInputDoc.setText(fileSelected);
		}
		else{
			this.txtInputDoc.setText("");
		}
		
		
		
	}

	public void setOutputPathSelected() {
		DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		directoryDialog.setText("Choose a directory");
		directoryDialog.setFilterPath("c://");
		outputPath = directoryDialog.open();
		System.out.println("outputPath Selected  " + outputPath);
		this.txtOutputDoc.setText("");
		if (outputPath==null ){
			System.out.println("outputPath is not selected " );
		}
		else{
			this.txtOutputDoc.setText(outputPath);
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
				job.setInputFile(new File (fileSelected));
				job.setOutputFile(new File(outputPath));
				job.setScript(scriptHandler);
				job.setStatus(Status.WAITING);
				Window.getInstance().addToQueue(job);
				
				//Doesn't work...keep looking, tried redraw() on the composite...
				//Window.getInstance().open();
				dispose();
		}
	}
	
	public void editConversion(Job job){
		txtConversionName.setText(job.getScript().getName());
		txtInputDoc.setText(job.getInputFile().getName());
		txtOutputDoc.setText(job.getOutputFile().getName());
	}
	
	
	
}
